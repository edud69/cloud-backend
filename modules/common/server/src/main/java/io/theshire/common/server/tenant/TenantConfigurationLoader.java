

package io.theshire.common.server.tenant;

import io.theshire.common.utils.security.encryptor.CryptoUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@Service
public class TenantConfigurationLoader {

 
  private static final String TENANT_DATASOURCE_CONFIG_QUERY =
      "select username, driver_class, initial_size, max_active, "
          + "max_idle, min_idle, max_wait, password, url "
          + "from tenant_datasources where tenant_id = ?";

 
  @Autowired
  private TenantConnectionProvider tenantConnectionProvider;

 
  @Autowired
  private CryptoUtils cryptoUtils;

 
  public TenantDataSourceConfig getTenantDataSourceConfig(final String tenantIdentifier)
      throws SQLException {
    final Connection connection = tenantConnectionProvider.getAnyConnection();
    final String sql = String.format("SET SCHEMA '%s'",
        TenantDatabaseSchema.DatabaseSchema.TENANT_CONFIG.getSchemaName());
    try (final PreparedStatement st = connection.prepareStatement(sql)) {
      st.execute();
      final JdbcTemplate jdbcTemplate =
          new JdbcTemplate(new SingleConnectionDataSource(connection, true));
      return buildDataSourceConfig(jdbcTemplate, tenantIdentifier);
    } finally {
      tenantConnectionProvider.releaseAnyConnection(connection);
    }
  }

 
  private TenantDataSourceConfig buildDataSourceConfig(final JdbcTemplate jdbcTemplate,
      final String tenantIdentifier) {
    return jdbcTemplate.queryForObject(TENANT_DATASOURCE_CONFIG_QUERY,
        new Object[] { tenantIdentifier }, new RowMapper<TenantDataSourceConfig>() {

        
          @Override
          public TenantDataSourceConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
              return TenantDataSourceConfig.builder().tenantIdentifier(tenantIdentifier)
                  .driveClass(rs.getString("driver_class")).initialSize(rs.getInt("initial_size"))
                  .maxActive(rs.getInt("max_active")).minIdle(rs.getInt("min_idle"))
                  .maxIdle(rs.getInt("max_idle")).maxWait(rs.getInt("max_wait"))
                  .username(rs.getString("username"))
                  .password(cryptoUtils.decrypt(rs.getString("password"))).url(rs.getString("url"))
                  .build();
            } catch (final Exception exc) {
              throw new RuntimeException(exc);
            }
          }

        });
  }

}
