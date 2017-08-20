

package io.theshire.common.server.tenant;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;


@Component
public class TenantConnectionProviderManager {

 
  private final Map<String, ConnectionProvider> tenantConnectionProviders =
      new ConcurrentHashMap<>();

 
  private final Map<String, Object> lazyLoadLocks = new ConcurrentHashMap<>();

 
  @Autowired
  private TenantConfigurationLoader tenantConfigurationLoader;

 
  public ConnectionProvider getAnyConnectionProvider() {
    return tenantConnectionProviders.get(TenantResolver.RESERVED_TENANT_IDENTIFIER);
  }

 
  public ConnectionProvider selectConnectionProvider(final String tenantIdentifier) {
    final ConnectionProvider cp = tenantConnectionProviders.get(tenantIdentifier);
    return cp != null ? cp : lazyLoadConnectionProvider(tenantIdentifier);
  }

 
  private ConnectionProvider lazyLoadConnectionProvider(final String tenantIdentifier) {
    final Object newLock = new Object();
    Object lock = lazyLoadLocks.putIfAbsent(tenantIdentifier, newLock);
    if (lock == null) {
      lock = newLock;
    }

    synchronized (lock) {
      ConnectionProvider cp = tenantConnectionProviders.get(tenantIdentifier);
      if (cp != null) {
        lazyLoadLocks.remove(tenantIdentifier);
        return cp;
      }

      try {
        final TenantDataSourceConfig tenantDs =
            tenantConfigurationLoader.getTenantDataSourceConfig(tenantIdentifier);
        cp = tenantConnectionProviders.put(tenantIdentifier, buildDataSource(tenantDs));
        lazyLoadLocks.remove(tenantIdentifier);

        return cp;
      } catch (final SQLException exception) {
        throw new RuntimeException(exception);
      }
    }
  }

 
  private ConnectionProvider buildDataSource(final TenantDataSourceConfig dsConfig) {
    final PoolProperties poolProperties = new PoolProperties();
    poolProperties.setDriverClassName(dsConfig.getDriveClass());
    poolProperties.setInitialSize(dsConfig.getInitialSize());
    poolProperties.setMaxActive(dsConfig.getMaxActive());
    poolProperties.setMaxIdle(dsConfig.getMaxIdle());
    poolProperties.setMaxWait(dsConfig.getMaxWait());
    poolProperties.setMinIdle(dsConfig.getMinIdle());
    poolProperties.setPassword(dsConfig.getPassword());
    poolProperties.setUrl(dsConfig.getUrl());
    poolProperties.setUsername(dsConfig.getUsername());
    poolProperties.setName(TenantDatabaseSchema.DatabaseSchema.TENANT_PREFIX.getSchemaName() + "-"
        + dsConfig.getTenantIdentifier());

    final DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
    return buildConnectionProvider(dataSource, dsConfig.getTenantIdentifier());
  }

 
  private ConnectionProvider buildConnectionProvider(final DataSource dataSource,
      final String tenantIdentifier) {
    final DatasourceConnectionProviderImpl cp = new DatasourceConnectionProviderImpl();
    cp.setDataSource(dataSource);
    cp.configure(new HashMap<String, String>());
    tenantConnectionProviders.put(tenantIdentifier, cp);
    return cp;
  }

 
  @Autowired(required = false)
  protected void initMainProvider(final DataSource dataSource) {
    buildConnectionProvider(dataSource, TenantResolver.RESERVED_TENANT_IDENTIFIER);
  }
}
