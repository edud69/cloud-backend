

package io.theshire.admin.service.tenant;

import io.theshire.common.utils.security.permission.constants.SecurityPermissionConstants;

import org.springframework.security.access.prepost.PreAuthorize;


public interface TenantRemoveService {

 
  @PreAuthorize("hasPermission(#tenantId, '" + SecurityPermissionConstants.TENANT_REMOVE + "')")
  void removeTenant(final String tenantId);

}
