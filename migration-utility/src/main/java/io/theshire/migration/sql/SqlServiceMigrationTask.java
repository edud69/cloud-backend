

package io.theshire.migration.sql;

import io.theshire.migration.sql.SqlTenantNodeLister.TenantNode;
import io.theshire.migration.utils.execution.MigrationJobExecutor;

import lombok.extern.slf4j.Slf4j;

import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.sql.DataSource;


@Slf4j
public abstract class SqlServiceMigrationTask extends SqlMigrationTask {

 
  public SqlServiceMigrationTask(String username, String password, String url) {
    super(username, password, url);
  }

 
  private static final String TEMPLATE_SCHEMA = "template";

 
  protected static final String CONFIGURATIONS_SCHEMA = "configurations";

 
  private static final String SCRIPTS_CONFIGURATIONS_FOLDER = "configurations";

 
  private static final String SCRIPTS_TEMPLATE_FOLDER = "template";

 
  private static final String MICRO_SERVICE_ROOT_FOLDER = "micro-services";

 
  private static final String MASTER_TENANT_NAME = "master";

 
  protected int migrateConfigurationsSchema() throws Exception {
    final List<String> schemas = new ArrayList<>(1);
    schemas.add(CONFIGURATIONS_SCHEMA);
    final Flyway flyway = prepareFlyway(schemas, getConfigurationScriptsSearchPath());
    final int result = flyway.migrate();

    // if main node was just created, then automatically add the entry in the configuration schema
    if (this.isMainNodeCreatedFromThisExecution) {
      this.insertNewMainNodeInConfigSchema();
    }

    return result;
  }

 
  private void insertNewMainNodeInConfigSchema() throws Exception {
    final DataSource ds = this.getMainDataSource();
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

    final String sql =
        String.format("INSERT INTO %s.tenant_datasources(tenant_id, url, " + "username, password, "
            + "driver_class, max_wait, max_active, initial_size, max_idle, min_idle) VALUES ("
            + "?,?,?,?,?,?,?,?,?,?)", CONFIGURATIONS_SCHEMA);

    final Object[] params = new Object[] { MASTER_TENANT_NAME, this.getConnectionUrl(),
        this.getUsername(), this.getEncryptedPassword(), DRIVER_CLASSNAME, 15, 10, 5, 7, 5 };

    jdbcTemplate.update(sql, params);
  }

 
  protected int migrateTemplateSchema() throws Exception {
    final List<String> schemas = new ArrayList<>(1);
    schemas.add(TEMPLATE_SCHEMA);

    // migrate the template database
    final Flyway flywayForDbTemplate = prepareFlyway(schemas, getTemplateScriptsSearchPath());
    flywayForDbTemplate.setDataSource(
        new SimpleDriverDataSource((Driver) Class.forName(DRIVER_CLASSNAME).newInstance(),
            getDbTemplateConnectionUrl(), this.getUsername(), this.getPassword()));
    flywayForDbTemplate.migrate();

    // migrate the master node database
    final Flyway flyway = prepareFlyway(schemas, getTemplateScriptsSearchPath());
    return flyway.migrate();
  }

 
  private int migrateTemplateSchema(final DataSource ds) {
    final List<String> schemas = new ArrayList<>(1);
    schemas.add(TEMPLATE_SCHEMA);
    final Flyway flyway = prepareFlyway(schemas, getTemplateScriptsSearchPath());
    flyway.setDataSource(ds);
    return flyway.migrate();
  }

 
  protected void migrateTenants() throws Exception {
    // migrates tenantOn main node
    doMigrateTenants(getMainDataSource(), "masterNode");
    // migrates tenants on other nodes
    final List<TenantNode> nodes = SqlTenantNodeLister.getTenantNodes(getMainDataSource());
    if (!nodes.isEmpty()) {
      for (final TenantNode n : nodes) {
        final DataSource dataSource = buildDataSource(n.getUsername(), n.getPassword(), n.getUrl());
        migrateTemplateSchema(dataSource);
        doMigrateTenants(dataSource, n.getUrl());
      }
    }
  }

 
  private void doMigrateTenants(final DataSource ds, final String nodeName) throws Exception {
    final List<String> tenantOnNode = SqlTenantSchemaLister.getTenantSchemas(ds);
    if (!tenantOnNode.isEmpty()) {
      log.info("Creating async tasks for tenant migration for service: {} on node : {}.",
          getServiceFolderName(), nodeName);
      final List<Runnable> rs = new ArrayList<>(tenantOnNode.size());
      tenantOnNode.forEach(t -> rs.add(createTenantMigrationJob(t, ds)));
      final List<Future<?>> fs = MigrationJobExecutor.submitAll(rs);
      int progress = 0;
      for (final Future<?> f : fs) {
        f.get();
        progress++;
        log.info("Completed tenant migration {} of {} for service: {} on node : {}.", progress,
            tenantOnNode.size(), getServiceFolderName(), nodeName);
      }
      log.info("Completed all tasks for tenant migration for service: {} on node : {}.",
          getServiceFolderName(), nodeName);
    }
  }

 
  private Runnable createTenantMigrationJob(final String tenant, final DataSource datasource) {
    return () -> {
      final List<String> tenantSchema = new ArrayList<>(1);
      tenantSchema.add(tenant);
      final Flyway flyway = prepareFlyway(tenantSchema, getTemplateScriptsSearchPath());
      flyway.setDataSource(datasource);
      flyway.migrate();
    };
  }


  protected final String getScriptSearchPath() {
    final String microServicePath = isMicroService() ? "/" + MICRO_SERVICE_ROOT_FOLDER + "/" : "/";
    return super.getScriptSearchPath() + microServicePath + getServiceFolderName();
  }

 
  protected final String getTemplateScriptsSearchPath() {
    return getScriptSearchPath() + "/" + SCRIPTS_TEMPLATE_FOLDER;
  }

 
  protected final String getConfigurationScriptsSearchPath() {
    return getScriptSearchPath() + "/" + SCRIPTS_CONFIGURATIONS_FOLDER;
  }

 
  protected abstract String getServiceFolderName();

 
  protected abstract boolean isMicroService();
}
