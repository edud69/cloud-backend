

package io.theshire.common.server.tenant.manager;

import io.theshire.common.utils.security.permission.constants.SecurityPermissionConstants;

import org.springframework.security.access.prepost.PreAuthorize;


interface TenantInitializer {

 
  @PreAuthorize("hasPermission(#tenantId, '" + SecurityPermissionConstants.TENANT_CREATE + "')")
  void initialize(final String tenantId, final TenantSqlEndpointMessage sqlEndpoint)
      throws Exception;
}
