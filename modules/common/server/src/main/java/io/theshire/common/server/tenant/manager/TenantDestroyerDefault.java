

package io.theshire.common.server.tenant.manager;

import io.theshire.common.server.configuration.elasticsearch.ElasticsearchConfigurer;
import io.theshire.common.server.configuration.jpa.JpaConfigurer;
import io.theshire.common.server.tenant.TenantConfigurationLoader;
import io.theshire.common.server.tenant.TenantDataSourceConfig;
import io.theshire.common.server.tenant.TenantDatabaseSchema;
import io.theshire.common.server.tenant.TenantResolver;
import io.theshire.common.service.infrastructure.indexation.IndexationTenantAwareService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Driver;

import javax.sql.DataSource;


@Slf4j
@Component
public class TenantDestroyerDefault implements TenantDestroyer {

 
  @Autowired
  private IndexationTenantAwareService indexationTenantAwareService;

 
  @Autowired
  private TenantConfigurationLoader tenantConfigurationLoader;

 
  @Autowired(required = false)
  private DataSource dataSource;

 
  private static final String SQL_DROP_SCHEMA = "DROP SCHEMA IF EXISTS %s CASCADE";

 
  private static final String SQL_DELETE_CONFIG =
      "DELETE FROM configurations.tenant_datasources WHERE tenant_id = ?";


  @Override
  public void destroy(String tenantId) throws Exception {
    log.debug("Removing tenant : {}.", tenantId);
    removeSearchEngineIndex(tenantId);
    removeDatabases(tenantId);
  }

 
  protected void removeSearchEngineIndex(final String tenantId) {
    if (ElasticsearchConfigurer.isActive()) {
      log.debug("Removing tenant search engine data for tenant : {}.", tenantId);
      String tid = TenantResolver.getTenantIdentifier();
      try {
        TenantResolver.setTenantIdentifier(tenantId);
        indexationTenantAwareService.deleteAll();
      } finally {
        TenantResolver.setTenantIdentifier(tid);
      }
    } else {
      log.debug("Elasticsearch not active on this service.");
    }
  }

 
  protected void removeDatabases(final String tenantId) throws Exception {
    if (!JpaConfigurer.isActive()) {
      log.debug("No JPA persistence active for for this service.");
      return;
    }

    log.debug("Removing tenant SQL data for tenant : {}.", tenantId);

    final TenantDataSourceConfig dsConfig =
        tenantConfigurationLoader.getTenantDataSourceConfig(tenantId);
    final SimpleDriverDataSource ds =
        new SimpleDriverDataSource((Driver) Class.forName(dsConfig.getDriveClass()).newInstance(),
            dsConfig.getUrl(), dsConfig.getUsername(), dsConfig.getPassword());
    final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(ds);
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(transactionManager.getDataSource());
    final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

    final Exception exception = transactionTemplate.execute(status -> {
      try {
        // delete schema
        String sql = String.format(SQL_DROP_SCHEMA,
            TenantDatabaseSchema.DatabaseSchema.TENANT_PREFIX.getSchemaName() + tenantId);
        jdbcTemplate.execute(sql);

        // delete configuration entry
        final JdbcTemplate jdbcTemplateForConfigDeletion = new JdbcTemplate(dataSource);
        jdbcTemplateForConfigDeletion.update(SQL_DELETE_CONFIG, tenantId);

        return null;
      } catch (final Exception exc) {
        return exc;
      }
    });

    if (exception != null) {
      throw exception;
    }
  }

}
