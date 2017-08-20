

package io.theshire.common.server.tenant;

import lombok.Getter;


public final class TenantDatabaseSchema {

 
  public static final String SINGLE_SIGN_ON_SCHEMA = "sso";

 
  public static final String MASTER_TENANT_NAME = "master";

 
  public static enum DatabaseSchema {

   
    SYSADMIN("admin"),

   
    SINGLE_SIGN_ON(SINGLE_SIGN_ON_SCHEMA),

   
    TENANT_TEMPLATE("template"),

   
    TENANT_PREFIX("tenant"),

   
    TENANT_CONFIG("configurations");

   

   
    @Getter
    private final String schemaName;

   
    private DatabaseSchema(final String schema) {
      this.schemaName = schema;
    }

  }
}
