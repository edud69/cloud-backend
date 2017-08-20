

package io.theshire.admin.service.impl.authentication;

import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_authorization;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_contentType;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_xTenantId;

import com.netflix.hystrix.exception.HystrixRuntimeException;

import io.theshire.admin.service.authentication.AdminPanelAuthenticationService;
import io.theshire.common.service.infrastructure.bridge.MicroserviceBridgeService;
import io.theshire.common.utils.authentication.OAuth2AuthenticationManagerBuilder;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


@Service
class AdminPanelAuthenticationServiceImpl implements AdminPanelAuthenticationService {

 
  private static final String HTTP_BASIC_AUTH_PREFIX = "Basic ";

 
  private static final String ADMIN_PANEL_TENANT = "master";

 
  private static final String HTTP_APPLICATION_JSON = "application/json";

 
  @Autowired
  private MicroserviceBridgeService microserviceBridgeService;

 
  private OAuth2AuthenticationManager authenticationManager;

 
  @Autowired
  protected void initAuthenticationManager(final JwtAccessTokenConverter jwtTokenEnhancer) {
    this.authenticationManager = new OAuth2AuthenticationManagerBuilder()
        .resource(OAuth2ResourceIdentifier.AdminPanel).jwtTokenEnhancer(jwtTokenEnhancer).build();
  }


  @Override
  public OAuth2Authentication authenticate(String username, String password) {
    final String auth = username + ":" + password;
    final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
    final String authorization =
        HTTP_BASIC_AUTH_PREFIX + new String(encodedAuth, Charset.forName("UTF-8"));

    final Map<String, String> headers = new HashMap<>();
    headers.put(HTTP_HEADER_authorization, authorization);
    headers.put(HTTP_HEADER_xTenantId, ADMIN_PANEL_TENANT);
    headers.put(HTTP_HEADER_contentType, HTTP_APPLICATION_JSON);

    final String body = "";
    OAuth2AccessToken oauth2Token;

    try {
      oauth2Token = microserviceBridgeService.invokeRestCall(OAuth2ResourceIdentifier.AuthService,
          "/admin/login", headers, HttpMethod.POST, body, OAuth2AccessToken.class);
    } catch (final Exception exc) {
      HttpStatusCodeException statusCodeExc = null;
      if (exc instanceof HttpStatusCodeException) {
        statusCodeExc = (HttpStatusCodeException) exc;
      } else if (exc instanceof HystrixRuntimeException) {
        final HystrixRuntimeException hystrixExc = (HystrixRuntimeException) exc;
        if (hystrixExc.getCause() instanceof HttpStatusCodeException) {
          statusCodeExc = (HttpStatusCodeException) hystrixExc.getCause();
        }
      }

      if (statusCodeExc != null && statusCodeExc.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
        return null;
      } else {
        throw exc;
      }
    }

    final String accessTokenValue = oauth2Token.getValue();

    // Authenticate the user with oauth2 token
    final PreAuthenticatedAuthenticationToken oauth2TokenAuth =
        new PreAuthenticatedAuthenticationToken(accessTokenValue, "");
    final OAuth2Authentication authentication =
        (OAuth2Authentication) authenticationManager.authenticate(oauth2TokenAuth);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    authentication.setDetails(oauth2Token);

    return authentication;
  }


  @Override
  public OAuth2Authentication refreshAuthentication(OAuth2RefreshToken refreshToken) {
    if (refreshToken == null) {
      return null;
    }

    final Map<String, String> headers = new HashMap<>();
    headers.put(HTTP_HEADER_xTenantId, ADMIN_PANEL_TENANT);

    OAuth2AccessToken oauth2Token;

    try {
      oauth2Token = microserviceBridgeService.invokeRestCall(OAuth2ResourceIdentifier.AuthService,
          "/admin/token/refresh?refresh_token=" + refreshToken.getValue(), headers, HttpMethod.POST,
          "", OAuth2AccessToken.class);
    } catch (final HttpStatusCodeException exc) {
      if (exc.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
        return null;
      } else {
        throw exc;
      }
    }

    // Authenticate the user with oauth2 token
    final PreAuthenticatedAuthenticationToken oauth2TokenAuth =
        new PreAuthenticatedAuthenticationToken(oauth2Token.getValue(), "");
    final OAuth2Authentication authentication =
        (OAuth2Authentication) authenticationManager.authenticate(oauth2TokenAuth);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    authentication.setDetails(oauth2Token);

    return authentication;
  }

}
