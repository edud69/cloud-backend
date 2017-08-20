

package io.theshire.migration.sql;

import io.theshire.migration.utils.crypto.CryptoDataDecoder;
import io.theshire.migration.utils.env.EnvironmentUtils;

import lombok.extern.slf4j.Slf4j;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;




@Slf4j
public abstract class SqlMigrationTask {

 
  private static final String MAINTENANCE_DB_NAME = "postgres";

 
  private static final String SCRIPTS_ROOT = "sql-scripts";

 
  protected static final String DRIVER_CLASSNAME = "org.postgresql.Driver";

 
  private static final String CREATE_DB_SQL_QUERY = "CREATE DATABASE %s";

 
  private static final String TEMPLATE_SUFFIX_NAME = "_template";

 
  private static final String DB_EXISTS_SQL_QUERY =
      "select exists (select * from " + "pg_catalog.pg_database where datname = ?)";

 
  private final Set<org.apache.tomcat.jdbc.pool.DataSource> tomcatJdbcDataSources = new HashSet<>();

 
  private final String mainNodeDatabaseName;

 
  private final String connectionUrl;

 
  private final String username;

 
  private final String password;

 
  private DataSource dataSource;

  private boolean createTemplateDb = true;

 
  protected boolean isMainNodeCreatedFromThisExecution;

 
  protected String getConnectionUrl() {
    return this.connectionUrl;
  }

 
  protected String getUsername() {
    return this.username;
  }

 
  protected String getEncryptedPassword() throws Exception {
    if (this.password != null) {
      return CryptoDataDecoder.getCryptoUtils().encrypt(this.password);
    }
    return null;
  }

 
  protected String getPassword() {
    return this.password;
  }

 
  private static String getDbNameFromUrl(final String url) {
    return url.substring(url.lastIndexOf("/") + 1, url.length());
  }

 
  protected static String getRootUrl(final String url) {
    return url.substring(0, url.lastIndexOf("/"));
  }

 
  private static final String getMaintenanceDbConnectionStrUrl(final String url) {
    return getRootUrl(url) + "/" + MAINTENANCE_DB_NAME;
  }

 
  protected String getDbTemplateConnectionUrl() {
    return this.connectionUrl.substring(0, connectionUrl.lastIndexOf("_")) + TEMPLATE_SUFFIX_NAME;
  }

 
  public SqlMigrationTask(final String username, final String password, final String url) {
    this.mainNodeDatabaseName = getDbNameFromUrl(url);
    this.connectionUrl = url;
    this.username = username;
    this.password = password;
  }

 
  protected void closeResources() {
    tomcatJdbcDataSources.forEach(ds -> {
      try {
        ds.close();
      } catch (final Exception exception) {
        log.error("Failed to close datasource, trace: ", exception);
      }
    });

    dataSource = null;
    tomcatJdbcDataSources.clear();
  }

  public void setCreateTemplateDb(boolean create) {
    this.createTemplateDb = create;
  }

 
  public final void execute() throws Exception {
    try {
      createDbIfNotExists(this.mainNodeDatabaseName);

      if (createTemplateDb) {
        final String templateDbName =
            this.mainNodeDatabaseName.substring(0, this.mainNodeDatabaseName.lastIndexOf("_"))
                + TEMPLATE_SUFFIX_NAME;
        createDbIfNotExists(templateDbName);

        if (dataSource != null) {
          throw new SQLException(
              "Previous datasource must be closed before " + "attempting a new execution.");
        }
      }

      this.dataSource = buildDataSource(username, password, connectionUrl);
      doExecute();

      if (EnvironmentUtils.isDevModeEnabled()) {
        this.doExecuteDevModeActions();
      }
    } finally {
      closeResources();
    }
  }

 
  protected abstract void doExecute() throws Exception;

 
  protected DataSource buildDataSource(final String username, final String password,
      final String url) {
    // + 1 because flyway uses 2 connections
    final int processors = Runtime.getRuntime().availableProcessors() * 2 + 1;

    final PoolProperties poolProperties = new PoolProperties();
    poolProperties.setDriverClassName(DRIVER_CLASSNAME);
    poolProperties.setInitialSize(processors);
    poolProperties.setMaxActive(processors);
    poolProperties.setMaxIdle(processors);
    poolProperties.setMinIdle(processors);
    poolProperties.setPassword(password);
    poolProperties.setUrl(url);
    poolProperties.setUsername(username);

    final org.apache.tomcat.jdbc.pool.DataSource ds =
        new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
    tomcatJdbcDataSources.add(ds);

    return ds;
  }

 
  protected Flyway prepareFlyway(final List<String> schemas, final String locations) {
    final Flyway flyway = doPrepareFlyway(schemas, locations);
    flyway.setDataSource(dataSource);
    return flyway;
  }

 
  private Flyway doPrepareFlyway(final List<String> schemas, final String locations) {
    final Flyway flyway = new Flyway();
    final String[] schemasArr = new String[schemas.size()];
    flyway.setBaselineOnMigrate(true);
    flyway.setSchemas(schemas.toArray(schemasArr));
    flyway.setLocations(locations);
    flyway.setSqlMigrationPrefix("");
    flyway.setOutOfOrder(true);
    return flyway;
  }

 
  protected Flyway prepareFlywayDetailed(final String username, final String password,
      final String url, final List<String> schemas, final String locations) {
    final Flyway flyway = doPrepareFlyway(schemas, locations);
    flyway.setDataSource(buildDataSource(username, password, url));
    return flyway;
  }

 
  protected String getScriptSearchPath() {
    return SCRIPTS_ROOT;
  }

 
  public final DataSource getMainDataSource() {
    return this.dataSource;
  }

 
  protected void doExecuteDevModeActions() throws Exception {
  }

 
  private void createDbIfNotExists(final String dbName) throws Exception {
    log.info("Creating new database node database for {} if needed.", dbName);

    final String maintenanceDbConnectionStrUrl =
        getMaintenanceDbConnectionStrUrl(this.connectionUrl);
    try (final Connection conn =
        DriverManager.getConnection(maintenanceDbConnectionStrUrl, username, password)) {
      try (final PreparedStatement ps = conn.prepareStatement(DB_EXISTS_SQL_QUERY)) {
        ps.setString(1, dbName);
        try (final ResultSet rs = ps.executeQuery()) {
          if (!rs.next() || !rs.getBoolean(1)) {
            // Create the database, it does not exists
            try (final Statement s = conn.createStatement()) {
              log.info("Database is creation ({}), ... .", dbName);
              String sql = String.format(CREATE_DB_SQL_QUERY, dbName);
              s.executeUpdate(sql);

              if (dbName.equals(this.mainNodeDatabaseName)) {
                this.isMainNodeCreatedFromThisExecution = true;
              }

              log.info("Database created: {}.", dbName);
            }
          }
        }
      }
    }
  }

}
