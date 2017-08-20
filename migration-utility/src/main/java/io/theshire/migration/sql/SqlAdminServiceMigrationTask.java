

package io.theshire.migration.sql;

import org.flywaydb.core.Flyway;

import java.util.ArrayList;
import java.util.List;

public class SqlAdminServiceMigrationTask extends SqlMigrationTask {

  public SqlAdminServiceMigrationTask(String username, String password, String url) {
    super(username, password, url);
    this.setCreateTemplateDb(false);
  }

  private static final String SCRIPTS_SERVICE_FOLDER = "admin-service";

  private static final String DEFAULT_SCHEMA_NAME = "public";

  @Override
  protected void doExecute() throws Exception {
    final List<String> schemas = new ArrayList<>(1);
    schemas.add(DEFAULT_SCHEMA_NAME);
    final Flyway flyway =
        prepareFlyway(schemas, getScriptSearchPath() + "/" + SCRIPTS_SERVICE_FOLDER);
    flyway.migrate();
  }

}
