

package io.theshire.migration;

import io.theshire.common.utils.security.encryptor.CryptoUtils;
import io.theshire.migration.constants.EnvConstants;
import io.theshire.migration.sql.SqlAccountServiceMigrationTask;
import io.theshire.migration.sql.SqlAdminServiceMigrationTask;
import io.theshire.migration.sql.SqlAuthServiceMigrationTask;
import io.theshire.migration.sql.SqlChatServiceMigrationTask;
import io.theshire.migration.sql.SqlDocumentServiceMigrationTask;
import io.theshire.migration.sql.SqlSecurityMigrationTask;
import io.theshire.migration.utils.crypto.CryptoDataDecoder;
import io.theshire.migration.utils.execution.MigrationJobExecutor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
public class Launcher {

 
  public static final void main(final String[] args) {
    log.info("Migration started.");

    final long startTime = System.currentTimeMillis();
    boolean success = false;

    if (args.length < 3) {
      log.error("Program arguments should be 1: dbUsername, 2: dbPassword, 3: encryptionKey, "
          + "(optional 4: {}).", EnvConstants.DEV_MODE_ARG);
      return;
    }

    try {
      // main datasource username
      final String username = args[0];
      // main datasource password
      final String password = args[1];
      // encryption key for password storage in database
      final String encryptionKey = args[2];

      // run dev initialization scripts
      if (args.length == 4 && args[3].equals(EnvConstants.DEV_MODE_ARG)) {
        log.info("Dev Mode enabled.");
        System.setProperty(EnvConstants.DEV_MODE_ARG, "true");
      }

      CryptoDataDecoder.initialize(new CryptoUtils(encryptionKey));

      // data updates
      new SqlAdminServiceMigrationTask(username, password,
          "jdbc:postgresql://docker-sql:5432/admin").execute();
      new SqlAuthServiceMigrationTask(username, password,
          "jdbc:postgresql://docker-sql:5432/authentication_master").execute();
      new SqlAccountServiceMigrationTask(username, password,
          "jdbc:postgresql://docker-sql:5432/account_master").execute();
      new SqlDocumentServiceMigrationTask(username, password,
          "jdbc:postgresql://docker-sql:5432/document_master").execute();
      new SqlChatServiceMigrationTask(username, password,
          "jdbc:postgresql://docker-sql:5432/chat_master").execute();

      // security updates
      new SqlSecurityMigrationTask(username, password,
          "jdbc:postgresql://docker-sql:5432/authentication_master").execute();

      success = true;
    } catch (final Exception exception) {
      log.error("Exception occurred, trace : ", exception);
    } finally {
      logTime(startTime, success);
      try {
        MigrationJobExecutor.shutDown();
      } catch (final Exception exception) {
        log.error("Cannot close thread pool, trace: ", exception);
      }
    }
  }

 
  private static final void logTime(final long startTime, final boolean success) {
    long endTime = System.currentTimeMillis();
    long timeToCompleteRequest = endTime - startTime;
    final String timeLog = String.format("%02dHH:%02dmm:%02dss:%03dms",
        TimeUnit.MILLISECONDS.toHours(timeToCompleteRequest),
        TimeUnit.MILLISECONDS.toMinutes(timeToCompleteRequest)
            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToCompleteRequest)),
        TimeUnit.MILLISECONDS.toSeconds(timeToCompleteRequest)
            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToCompleteRequest)),
        timeToCompleteRequest
            - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(timeToCompleteRequest)));
    log.info("Migration completed with {}. Completed in : {}.", success ? "SUCCESS" : "FAILURE",
        timeLog);
  }

}
