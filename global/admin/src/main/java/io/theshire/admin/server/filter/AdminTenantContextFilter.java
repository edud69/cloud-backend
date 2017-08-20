

package io.theshire.admin.server.filter;

import io.theshire.common.server.tenant.TenantContextFilter;
import io.theshire.common.server.tenant.TenantDatabaseSchema;


public class AdminTenantContextFilter extends TenantContextFilter {


  protected String loadTenantIdentifier() {
    return TenantDatabaseSchema.DatabaseSchema.SYSADMIN.getSchemaName();
  }

}
