

package io.theshire.common.server.tenant;

import io.theshire.common.utils.security.authentication.AuthenticationContext;
import io.theshire.common.utils.security.authentication.AuthenticationInfo;
import io.theshire.common.utils.security.constants.SecurityAuthenticationKeyDetailConstants;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


@Slf4j
public class TenantContextFilter extends GenericFilterBean {


  @Override
  public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
      throws IOException, ServletException {
    try {
      final String tenantIdentifier = loadTenantIdentifier();
      log.debug("TenantResolver is now setted to : " + tenantIdentifier + ".");
      verifyAnonymousTenantIdentifier(tenantIdentifier);
      TenantResolver.setTenantIdentifier(tenantIdentifier);
      arg2.doFilter(arg0, arg1);
    } finally {
      log.debug("TenantResolver now cleared, as request processing completed");
      TenantResolver.clearTenantIdentifier();
      AuthenticationContext.clear();
    }
  }

 
  private void verifyAnonymousTenantIdentifier(final String tenantIdentifier) {
    final AuthenticationInfo authInfo = AuthenticationContext.get();
    if (authInfo == null || authInfo.getTenantIdentifier() == null) {
      final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth instanceof AnonymousAuthenticationToken) {
        final AnonymousAuthenticationToken anonymousAuth = (AnonymousAuthenticationToken) auth;
        final Map<String, Object> details = new HashMap<>();
        details.put(SecurityAuthenticationKeyDetailConstants.TENANT_IDENTIFIER, tenantIdentifier);
        anonymousAuth.setDetails(details);
        AuthenticationContext.clear();
      }
    }
  }

 
  protected String loadTenantIdentifier() {
    return AuthenticationContext.get() != null ? AuthenticationContext.get().getTenantIdentifier()
        : null;
  }

}
