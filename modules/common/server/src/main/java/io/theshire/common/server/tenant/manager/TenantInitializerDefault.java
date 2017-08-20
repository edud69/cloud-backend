

package io.theshire.common.server.tenant.manager;

import io.theshire.common.server.configuration.elasticsearch.ElasticsearchConfigurer;
import io.theshire.common.server.configuration.jpa.JpaConfigurer;
import io.theshire.common.server.tenant.TenantDatabaseSchema.DatabaseSchema;
import io.theshire.common.server.tenant.TenantResolver;
import io.theshire.common.service.infrastructure.indexation.IndexationTenantAwareService;
import io.theshire.common.utils.security.encryptor.CryptoUtils;
import io.theshire.common.utils.sql.SqlDatabaseCreator;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Driver;

import javax.sql.DataSource;




@Slf4j
@Component
public class TenantInitializerDefault implements TenantInitializer {

 
  private static final String SQL_CLONE_SCHEMA = "select %s.clone_schema('%s', '%s')";

 
  private static final String SQL_ADD_TENANT_CONFIG =
      "insert into %s.tenant_datasources (tenant_id, url, username, password, driver_class, "
          + "max_wait, max_active, initial_size, max_idle, min_idle) values "
          + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING";

 
  private static final String MAINTENANCE_DB_NAME = "postgres";

 
  @Value("${app.cloud.database.maxTenantsPerDb}")
  private int maxTenantsPerDatabase;

 
  @Autowired
  private IndexationTenantAwareService indexationTenantAwareService;

 
  @Autowired(required = false)
  private DataSource datasource;

 
  @Autowired
  private CryptoUtils cryptoUtils;


  @Override
  public void initialize(final String tenantId, final TenantSqlEndpointMessage sqlEndpoint)
      throws Exception {
    log.debug("Creating tenant : {}.", tenantId);

    if (!JpaConfigurer.isActive()) {
      log.debug("No JPA persistence active for for this service.");
      indexSearchEngine(tenantId);
      return;
    } else if (sqlEndpoint == null) {
      throw new NullArgumentException("sqlEndpoint");
    }

    final DataSourceTransactionManager transactionManager =
        new DataSourceTransactionManager(datasource);
    final JdbcTemplate jdbcTemplateForConfigInsertion =
        new JdbcTemplate(transactionManager.getDataSource());
    final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

    final Exception exception = transactionTemplate.execute(status -> {
      try {
        // 1. Update DB Url if needed (create a new database or find one with available space)
        final SqlDatabaseCreator dbCreator = new SqlDatabaseCreator(sqlEndpoint.getUsername(),
            sqlEndpoint.getPassword(), sqlEndpoint.getUrl(), MAINTENANCE_DB_NAME,
            maxTenantsPerDatabase, sqlEndpoint.getDriveClass());
        final String tenantDbUrl = dbCreator.getDatabaseNameAndCreateIfNeeded();

        // 2. Insert tenant_datasources entry
        final String sqlInsertConfig =
            String.format(SQL_ADD_TENANT_CONFIG, DatabaseSchema.TENANT_CONFIG.getSchemaName());
        final Object[] params = new Object[] { tenantId, tenantDbUrl, sqlEndpoint.getUsername(),
            cryptoUtils.encrypt(sqlEndpoint.getPassword()), sqlEndpoint.getDriveClass(),
            sqlEndpoint.getMaxWait(), sqlEndpoint.getMaxActive(), sqlEndpoint.getInitialSize(),
            sqlEndpoint.getMaxIdle(), sqlEndpoint.getMinIdle() };
        jdbcTemplateForConfigInsertion.update(sqlInsertConfig, params);

        // 3. Create schemas
        final SimpleDriverDataSource ds = new SimpleDriverDataSource(
            (Driver) Class.forName(sqlEndpoint.getDriveClass()).newInstance(), tenantDbUrl,
            sqlEndpoint.getUsername(), sqlEndpoint.getPassword());
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

        createSchemas(tenantId, jdbcTemplate);
        indexSearchEngine(tenantId);

        return null;
      } catch (final Exception exc) {
        return exc;
      }
    });

    if (exception != null) {
      throw exception;
    }
  }

 
  protected void createSchemas(final String tenantId, final JdbcTemplate jdbcTemplate) {
    final String source = DatabaseSchema.TENANT_TEMPLATE.getSchemaName();
    final String target = DatabaseSchema.TENANT_PREFIX.getSchemaName() + tenantId;
    final String sql = String.format(SQL_CLONE_SCHEMA,
        DatabaseSchema.TENANT_TEMPLATE.getSchemaName(), source, target);
    jdbcTemplate.execute(sql);
  }

 
  protected void indexSearchEngine(final String tenantId) {
    if (ElasticsearchConfigurer.isActive()) {
      String tid = TenantResolver.getTenantIdentifier();
      try {
        TenantResolver.setTenantIdentifier(tenantId);
        indexationTenantAwareService.indexAll();
      } finally {
        TenantResolver.setTenantIdentifier(tid);
      }
    } else {
      log.debug("Elasticsearch not active on this service.");
    }
  }

}
