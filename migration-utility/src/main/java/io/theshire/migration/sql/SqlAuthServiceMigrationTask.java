

package io.theshire.migration.sql;

import org.flywaydb.core.Flyway;

import java.util.ArrayList;
import java.util.List;


public class SqlAuthServiceMigrationTask extends SqlServiceMigrationTask {

 
  public SqlAuthServiceMigrationTask(String username, String password, String url) {
    super(username, password, url);
  }

 
  private static final String SCRIPTS_SERVICE_FOLDER = "auth-service";

 
  private static final String SSO_SCHEMA = "sso";

 
  private static final String SSO_SCRIPTS_FOLDER = "sso";


  @Override
  protected void doExecute() throws Exception {
    // 1. Migrate configurations
    migrateConfigurationsSchema();
    // 2. Migrate sso
    migrateSsoSchema();
    // 3. Migrate template
    migrateTemplateSchema();
    // 4. Migrate tenants
    migrateTenants();
  }

 
  private int migrateSsoSchema() {
    final List<String> schemas = new ArrayList<>(1);
    schemas.add(SSO_SCHEMA);
    final Flyway flyway = prepareFlyway(schemas, getSsoScriptsSearchPath());
    return flyway.migrate();
  }

 
  private String getSsoScriptsSearchPath() {
    return getScriptSearchPath() + "/" + SSO_SCRIPTS_FOLDER;
  }


  @Override
  protected String getServiceFolderName() {
    return SCRIPTS_SERVICE_FOLDER;
  }


  @Override
  protected boolean isMicroService() {
    return false;
  }
}
