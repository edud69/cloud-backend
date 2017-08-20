

package io.theshire.common.server.tenant.manager;

import io.theshire.common.utils.security.permission.constants.SecurityPermissionConstants;

import org.springframework.security.access.prepost.PreAuthorize;


interface TenantDestroyer {

 
  @PreAuthorize("hasPermission(#tenantId, '" + SecurityPermissionConstants.TENANT_REMOVE + "')")
  void destroy(final String tenantId) throws Exception;

}
