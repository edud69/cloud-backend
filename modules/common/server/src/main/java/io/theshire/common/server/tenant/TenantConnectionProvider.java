

package io.theshire.common.server.tenant;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;


@Component
public class TenantConnectionProvider extends AbstractMultiTenantConnectionProvider {

 
  private static final long serialVersionUID = -2675511514984791417L;

 
  private static final Pattern tenantSchemaChars = Pattern.compile("[a-zA-Z0-9]+");

 
  @Autowired
  private transient TenantConnectionProviderManager tenantConnectionProviderManager;


  @Override
  protected ConnectionProvider getAnyConnectionProvider() {
    return tenantConnectionProviderManager.getAnyConnectionProvider();
  }


  @Override
  protected ConnectionProvider selectConnectionProvider(final String tenantIdentifier) {
    return tenantConnectionProviderManager.selectConnectionProvider(tenantIdentifier);
  }


  @Override
  public Connection getAnyConnection() throws SQLException {
    return doGetConnection(getAnyConnectionProvider());
  }


  @Override
  public void releaseConnection(String tenantIdentifier, Connection connection)
      throws SQLException {
    flipToDefaultSchemaAndCloseConnection(connection, tenantIdentifier);
  }


  @Override
  public void releaseAnyConnection(Connection connection) throws SQLException {
    flipToDefaultSchemaAndCloseConnection(connection, null);
  }

 
  private void flipToDefaultSchemaAndCloseConnection(final Connection connection,
      final String tenantIdentifier) throws SQLException {
    if (connection == null) {
      return;
    }

    flipToDefaultSchema(connection);

    final ConnectionProvider connProvider = tenantIdentifier == null ? getAnyConnectionProvider()
        : selectConnectionProvider(tenantIdentifier);
    if (connProvider != null) {
      connProvider.closeConnection(connection);
    }
  }


  @Override
  public Connection getConnection(final String tenantIdentifier) throws SQLException {
    if (tenantIdentifier == null) {
      return getAnyConnection();
    }

    final Connection connection = doGetConnection(selectConnectionProvider(tenantIdentifier));
    if (connection == null) {
      return connection;
    }

    String injectionSafeTenantId = null;
    // validate for SQL injections
    if (TenantResolver.RESERVED_TENANT_IDENTIFIER.equals(tenantIdentifier)) {
      injectionSafeTenantId = TenantDatabaseSchema.DatabaseSchema.TENANT_TEMPLATE.getSchemaName();
    } else {
      injectionSafeTenantId = tenantIdentifier.trim();
      validateIdentifier(injectionSafeTenantId);
      injectionSafeTenantId =
          TenantDatabaseSchema.DatabaseSchema.TENANT_PREFIX.getSchemaName() + injectionSafeTenantId;
    }
    final String sql = String.format("SET SCHEMA '%s'", injectionSafeTenantId);
    try (final PreparedStatement st = connection.prepareStatement(sql)) {
      st.execute();
    }

    return connection;
  }

 
  private void flipToDefaultSchema(final Connection connection) throws SQLException {
    if (connection == null) {
      return;
    }
    final String sql = String.format("SET SCHEMA '%s'",
        TenantDatabaseSchema.DatabaseSchema.TENANT_TEMPLATE.getSchemaName());
    try (final PreparedStatement st = connection.prepareStatement(sql)) {
      st.execute();
    }
  }

 
  private void validateIdentifier(final String tenantIdentifier) throws SQLException {
    if (!tenantSchemaChars.matcher(tenantIdentifier).matches()) {
      // a known SQL bug does not permit the use of PreparedStatement
      // with SET SCHEMA,
      // a manual check must be done
      throw new SQLException("Schema name does not contain a sequence of valid characters.");
    }
  }

 
  private Connection doGetConnection(final ConnectionProvider connProvider) throws SQLException {
    return connProvider == null ? null : connProvider.getConnection();
  }
}
