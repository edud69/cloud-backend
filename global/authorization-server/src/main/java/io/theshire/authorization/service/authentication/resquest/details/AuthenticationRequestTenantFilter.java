

package io.theshire.authorization.service.authentication.resquest.details;

import io.theshire.common.server.tenant.TenantContextFilter;


public class AuthenticationRequestTenantFilter extends TenantContextFilter {


  @Override
  protected String loadTenantIdentifier() {
    return AuthenticationRequestDetails.get().getTenantIdentifier();
  }

}
