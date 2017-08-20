

package io.theshire.migration.sql;

import io.theshire.migration.sql.SqlTenantNodeLister.TenantNode;
import io.theshire.migration.utils.execution.MigrationJobExecutor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;




@Slf4j
public class SqlSecurityMigrationTask extends SqlMigrationTask {

 
  private static final String ADMIN_USER_CREATION_SCRIPT =
      "sql-dev-scripts/admin-user-creation.sql";

 
  public SqlSecurityMigrationTask(String username, String password, String url) {
    super(username, password, url);
  }


  @Override
  protected void doExecuteDevModeActions() throws Exception {
    super.doExecuteDevModeActions();
    final Resource resource = new ClassPathResource(ADMIN_USER_CREATION_SCRIPT);
    ScriptUtils.executeSqlScript(this.getMainDataSource().getConnection(), resource);
  }


  @Override
  protected void doExecute() throws Exception {
    final List<Runnable> rs = new ArrayList<>();
    SqlTenantNodeLister.getTenantNodes(getMainDataSource())
        .forEach(tn -> rs.add(() -> executeForTenantsAsync(tn)));

    final int total = rs.size();
    final AtomicInteger progress = new AtomicInteger(0);

    log.info("Launching tasks for tenants security migration.");
    final List<Future<?>> fs = MigrationJobExecutor.submitAll(rs);
    for (final Future<?> f : fs) {
      f.get();
      log.debug("Node migration completed, total progression : {} / {} nodes migrated.",
          progress.incrementAndGet(), total);
    }

    log.info("Tenants security migration task completed.");
  }

 
  private void executeForTenantsAsync(final TenantNode node) {
    final DataSource ds = buildDataSource(node.getUsername(), node.getPassword(), node.getUrl());
    final List<String> tenants = SqlTenantSchemaLister.getTenantSchemas(ds);
    new SqlTenantSecurityUpdater(ds, "template").executeInTransaction();
    final int totalTenantsOnNode = tenants.size();
    final AtomicInteger progress = new AtomicInteger(0);

    log.debug("Applying security migration to {} on node {}.", tenants, node.getUrl());

    for (final String schema : tenants) {
      log.debug("Migrating tenant {} security on node {}.", schema, node.getUrl());
      new SqlTenantSecurityUpdater(ds, schema).executeInTransaction();
      log.debug("Tenant {} security migration applied on node {}, progress {} / {}.", schema,
          node.getUrl(), progress.incrementAndGet(), totalTenantsOnNode);
    }

    log.debug("Node security migration completed, node {}.", node.getUrl());
  }
}
