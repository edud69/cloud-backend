

package io.theshire.migration.sql;


public class SqlChatServiceMigrationTask extends SqlServiceMigrationTask {

 
  private static final String CHAT_SERVICE_FOLDER = "chat-service";

 
  public SqlChatServiceMigrationTask(String username, String password, String url) {
    super(username, password, url);
  }


  @Override
  protected String getServiceFolderName() {
    return CHAT_SERVICE_FOLDER;
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
