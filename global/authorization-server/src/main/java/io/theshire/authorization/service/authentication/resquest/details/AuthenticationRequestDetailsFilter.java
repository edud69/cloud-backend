

package io.theshire.authorization.service.authentication.resquest.details;

import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_xTenantId;

import io.theshire.authorization.server.session.PersistentSessionStrategy;
import io.theshire.common.server.tenant.TenantResolver;
import io.theshire.common.utils.security.authentication.AuthenticationDetails;
import io.theshire.common.utils.security.constants.SecurityAuthenticationKeyDetailConstants;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;




@Slf4j
public class AuthenticationRequestDetailsFilter extends GenericFilterBean {

 
  private static final String OAUTH2_STATE_PARAM = "state";

 
  private static final String TENANT_PARAM = "tenant";

 
  private final PersistentSessionStrategy sessionStrategy;

 
  public AuthenticationRequestDetailsFilter(
      final PersistentSessionStrategy persistentSessionStrategy) {
    this.sessionStrategy = persistentSessionStrategy;
  }


  @Override
  public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
      throws IOException, ServletException {
    try {
      log.debug("Setting request details for future authentication.");
      AuthenticationRequestDetails.set(buildDetails(arg0));
      arg2.doFilter(arg0, arg1);
    } finally {
      log.debug("TenantResolver now cleared, as request processing completed");
      AuthenticationRequestDetails.clear();
      TenantResolver.clearTenantIdentifier();
    }
  }

 
  private AuthenticationDetails buildDetails(final ServletRequest request) {
    final Map<String, Object> details = new HashMap<>();
    final HttpServletRequest httpRequest = (HttpServletRequest) request;

    String tid = httpRequest.getHeader(HTTP_HEADER_xTenantId);
    if (tid != null) {
      log.debug("Tenant identifier found in httpHeader ({}), tenantId={}.", HTTP_HEADER_xTenantId,
          tid);
    } else if (request.getParameter(OAUTH2_STATE_PARAM) != null) {
      tid = (String) sessionStrategy.getAttribute(request.getParameter(OAUTH2_STATE_PARAM),
          PersistentSessionStrategy.TENANT_ID);
      if (tid != null) {
        log.debug("Tenant identifier found in database from previous state ({}), tenantId={}.",
            PersistentSessionStrategy.TENANT_ID, tid);
      }
    } else if (request.getParameter(TENANT_PARAM) != null) {
      tid = request.getParameter(TENANT_PARAM);
      if (tid != null) {
        log.debug("Tenant identifier found in httpParameters ({}), tenantId={}.",
            PersistentSessionStrategy.TENANT_ID, tid);
      }
    }

    if (tid != null) {
      details.put(SecurityAuthenticationKeyDetailConstants.TENANT_IDENTIFIER, tid);
      TenantResolver.setTenantIdentifier(tid);
    }

    return new AuthenticationDetails(details);
  }

}
