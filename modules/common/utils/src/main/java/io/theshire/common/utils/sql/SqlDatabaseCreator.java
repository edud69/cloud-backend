

package io.theshire.common.utils.sql;

import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.sql.Driver;
import java.util.List;
import java.util.Map;
import java.util.Optional;




@Slf4j
public class SqlDatabaseCreator {

 
  protected static final String SQL_GET_DATABASE_WITH_AVAILABLE_TENANT_SPACE =
      "select datTbl.datName, "
          + "(case when schemaTbl.tenant_count is null then 0 else schemaTbl.tenant_count end) "
          + "from pg_database as datTbl left join "
          + "(select catalog_name, count(distinct schema_name) as tenant_count "
          + "from information_schema.schemata "
          + "where schema_name like 'tenant%' group by catalog_name) schemaTbl "
          + "on schemaTbl.catalog_name = datTbl.datName "
          + "where datname like ? and (tenant_count < ? or tenant_count is null)";

 
  protected static final String SQL_GET_NEXT_AVAILABLE_SLAVE_DB_ID =
      "select case when nextnumber is null then '000' else nextnumber end "
          + "from (select max(replace(datName, ?, '')) as nextnumber "
          + "from pg_database where datname like ?) dbNamesTbl";

 
  private static final String CREATE_DB_SQL_QUERY = "CREATE DATABASE %s WITH TEMPLATE %s";

 
  private static final String DB_TENANT_DATA_SUFFIX = "_slave";

 
  private static final String DB_DATA_TEMPLATE_SUFFIX = "_template";

 
  private static final String DB_TENANT_MASTERNODE_SUFFIX = "_master";

 
  private final String maintenanceDatabaseUrl;

 
  private final String username;

 
  private final String password;

 
  private final int maxTenantPerDatabase;

 
  private final String driverClassName;

 
  private String dbServiceNamePrefix;

 
  public SqlDatabaseCreator(final String username, final String password, final String originalUrl,
      final String maintenanceDbName, final int maxTenantPerDatabase,
      final String driverClassName) {
    this.username = username;
    this.password = password;
    this.maintenanceDatabaseUrl = getMaintenanceDbConnectionStrUrl(originalUrl, maintenanceDbName);
    this.maxTenantPerDatabase = maxTenantPerDatabase;
    this.driverClassName = driverClassName;
    this.dbServiceNamePrefix =
        getDbNameFromUrl(originalUrl).replace(DB_TENANT_MASTERNODE_SUFFIX, DB_TENANT_DATA_SUFFIX);
  }

 
  public String getDatabaseNameAndCreateIfNeeded() throws Exception {
    final JdbcTemplate jdbcTemplate = createJdbcTemplate();

    // 1. get available database with space for a new tenant
    final String dbPrefix = dbServiceNamePrefix + "%";
    final List<Map<String, Object>> rez =
        jdbcTemplate.queryForList(SQL_GET_DATABASE_WITH_AVAILABLE_TENANT_SPACE,
            new Object[] { dbPrefix, maxTenantPerDatabase });

    final Optional<String> dbName = rez.stream().map(row -> (String) row.get("datName")).findAny();
    if (dbName.isPresent()) {
      log.debug("A database with available space was found : {}.", dbName.get());
      return getDbConnectionStrUrlWithoutDbName(maintenanceDatabaseUrl) + "/" + dbName.get();
    }

    // 2. No more space available accross all db on the server, create a new one
    final Integer lastDbId = jdbcTemplate.queryForObject(SQL_GET_NEXT_AVAILABLE_SLAVE_DB_ID,
        Integer.class, new Object[] { dbServiceNamePrefix, dbServiceNamePrefix + "%" });
    final String databaseName = dbServiceNamePrefix + String.format("%03d", (lastDbId + 1));
    final String templateDbName =
        dbServiceNamePrefix.replace(DB_TENANT_DATA_SUFFIX, DB_DATA_TEMPLATE_SUFFIX);
    log.debug("Last slave database id is : {}.", lastDbId);

    // 3. Create the database
    log.info("No slave database with space available, creating a new database databaseName={}.",
        databaseName);
    jdbcTemplate.execute(String.format(CREATE_DB_SQL_QUERY, databaseName, templateDbName));
    log.info("DatabaseName={} was created.", databaseName);

    return getDbConnectionStrUrlWithoutDbName(maintenanceDatabaseUrl) + "/" + databaseName;
  }

 
  protected JdbcTemplate createJdbcTemplate()
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    final SimpleDriverDataSource ds =
        new SimpleDriverDataSource((Driver) Class.forName(driverClassName).newInstance(),
            maintenanceDatabaseUrl, username, password);
    return new JdbcTemplate(ds);
  }

 
  private String getDbConnectionStrUrlWithoutDbName(final String url) {
    return url.substring(0, url.lastIndexOf("/"));
  }

 
  private String getMaintenanceDbConnectionStrUrl(final String url,
      final String maintenanceDbName) {
    return getDbConnectionStrUrlWithoutDbName(url) + "/" + maintenanceDbName;
  }

 
  private String getDbNameFromUrl(final String url) {
    return url.substring(url.lastIndexOf("/") + 1, url.length());
  }
}
