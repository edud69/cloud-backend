

package io.theshire.migration.sql;


public class SqlDocumentServiceMigrationTask extends SqlServiceMigrationTask {

 
  private static final String DOCUMENT_SERVICE_FOLDER = "document-service";

 
  public SqlDocumentServiceMigrationTask(String username, String password, String url) {
    super(username, password, url);
  }


  @Override
  protected String getServiceFolderName() {
    return DOCUMENT_SERVICE_FOLDER;
  }


  @Override
  protected boolean isMicroService() {
    return true;
  }


  @Override
  protected void doExecute() throws Exception {
    // 1. migrate configurations
    migrateConfigurationsSchema();
    // 2. migrate template
    migrateTemplateSchema();
    // 3. migrate tenants
    migrateTenants();
  }

}
