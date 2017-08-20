

package io.theshire.admin.service.tenant;

import io.theshire.common.utils.security.permission.constants.SecurityPermissionConstants;

import org.springframework.security.access.prepost.PreAuthorize;


public interface TenantAddService {

 
  @PreAuthorize("hasPermission(#tenantId, '" + SecurityPermissionConstants.TENANT_CREATE + "')")
  void addNewTenant(final String tenantId);

 
  @PreAuthorize("hasPermission(#tenantId, '" + SecurityPermissionConstants.TENANT_CREATE + "')")
  void addNewTenant(final String tenantId, final String serviceName);

}
