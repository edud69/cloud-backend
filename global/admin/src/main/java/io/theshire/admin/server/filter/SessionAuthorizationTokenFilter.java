

package io.theshire.admin.server.filter;

import io.theshire.admin.service.authentication.AdminPanelAuthenticationService;
import io.theshire.common.utils.security.constants.SecurityAuthenticationKeyDetailConstants;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class SessionAuthorizationTokenFilter extends GenericFilterBean {

 
  private static final String OAUTH2_AUTHENTICATION = "oauth2_auth";

 
  private final AdminPanelAuthenticationService adminPanelAuthenticationService;

 
  public SessionAuthorizationTokenFilter(
      AdminPanelAuthenticationService adminPanelAuthenticationService) {
    this.adminPanelAuthenticationService = adminPanelAuthenticationService;
  }


  @SuppressWarnings("unchecked")
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    final HttpServletRequest httpRequest = (HttpServletRequest) request;
    final HttpSession session = httpRequest.getSession();
    if (session != null) {
      renewIfExpired(session);

      final OAuth2Authentication authentication =
          (OAuth2Authentication) session.getAttribute(OAUTH2_AUTHENTICATION);

      final OAuth2AccessToken accessToken = extractAccessToken(authentication);
      if (accessToken != null) {
        ((Map<String, Object>) authentication.getUserAuthentication().getDetails())
            .put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN, accessToken.getValue());
      }

      if (authentication != null) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    chain.doFilter(httpRequest, response);
  }

 
  private void renewIfExpired(final HttpSession session) {
    final OAuth2Authentication authentication =
        (OAuth2Authentication) session.getAttribute(OAUTH2_AUTHENTICATION);
    if (authentication != null) {
      final OAuth2AccessToken accessToken = extractAccessToken(authentication);
      if (accessToken != null && accessToken.isExpired()) {
        session.setAttribute(OAUTH2_AUTHENTICATION,
            adminPanelAuthenticationService.refreshAuthentication(accessToken.getRefreshToken()));
      }
    }
  }

 
  private OAuth2AccessToken extractAccessToken(final OAuth2Authentication auth) {
    if (auth != null) {
      final Authentication userAuth = auth.getUserAuthentication();
      if (userAuth != null) {
        @SuppressWarnings("unchecked")
        final Map<String, Object> details = (Map<String, Object>) (userAuth.getDetails());
        if (details != null) {
          return (OAuth2AccessToken) auth.getDetails();
        }
      }
    }

    return null;
  }

}
