

package io.theshire.common.service.infrastructure.impl.impersonification;

import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_authorization;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_xTenantId;

import io.theshire.common.service.infrastructure.bridge.MicroserviceBridgeService;
import io.theshire.common.service.infrastructure.impersonification.ImpersonificationService;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;
import io.theshire.common.utils.security.authentication.AuthenticationContext;
import io.theshire.common.utils.security.constants.SecurityAuthenticationKeyDetailConstants;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;


@Service
@Transactional
public class ImpersonificationServiceImpl implements ImpersonificationService {

 
  @Autowired
  private MicroserviceBridgeService crossTalkMicroserviceBridgeService;

 
  @Autowired
  private TokenStore tokenStore;

 
  @Autowired
  private AuthenticationManager authManager;

 
  @Value("${app.cloud.security.oauth.client.internal.id}")
  private String internalClientId;

 
  @Value("${app.cloud.security.oauth.client.internal.secret}")
  private String internalClientPassword;


  @Override
  public void runWithImpersonated(Runnable runnable, final String username, final String password) {
    final Authentication auth = getContext();

    try {
      switchContext(username, password);
      if (runnable != null) {
        runnable.run();
      }
    } finally {
      restoreContext(auth);
    }
  }

 
  private String basicHeaderValue() {
    String auth = internalClientId + ":" + internalClientPassword;
    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
    return "Basic " + new String(encodedAuth, Charset.forName("UTF-8"));
  }

 
  private void switchContext(final String username, final String password) {
    final Map<String, String> parameters = new HashMap<>();
    parameters.put("username", username);
    parameters.put("password", password);

    final Map<String, String> headers = new HashMap<>();
    headers.put(HTTP_HEADER_authorization, basicHeaderValue());
    if (AuthenticationContext.get() != null
        && AuthenticationContext.get().getTenantIdentifier() != null) {
      headers.put(HTTP_HEADER_xTenantId, AuthenticationContext.get().getTenantIdentifier());
    }

    final OAuth2AccessToken oauth2AccessToken = crossTalkMicroserviceBridgeService.invokeRestCall(
        OAuth2ResourceIdentifier.AuthService, "/oauth/token?grant_type=password", headers,
        HttpMethod.POST, parameters, OAuth2AccessToken.class);
    final OAuth2Authentication oauth2Authentication =
        tokenStore.readAuthentication(oauth2AccessToken);
    if (authManager instanceof OAuth2AuthenticationManager) {
      SecurityContextHolder.getContext()
          .setAuthentication(authManager.authenticate(oauth2Authentication));
    } else {
      final UsernamePasswordAuthenticationToken userAuth =
          ((UsernamePasswordAuthenticationToken) oauth2Authentication.getUserAuthentication());
      if (userAuth.getDetails() == null) {
        userAuth.setDetails(new HashMap<String, Object>());
      }
      @SuppressWarnings("unchecked")
      final Map<String, Object> details = (Map<String, Object>) userAuth.getDetails();
      details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN, oauth2AccessToken.getValue());
      SecurityContextHolder.getContext().setAuthentication(oauth2Authentication);
    }
  }

 
  private Authentication getContext() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

 
  private void restoreContext(final Authentication authentication) {
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }


  @Override
  public <T> T callWithImpersonated(Callable<T> callable, final String username,
      final String password) {
    final Authentication auth = getContext();

    try {
      switchContext(username, password);
      if (callable != null) {
        return callable.call();
      }
      return null;
    } catch (final Exception exception) {
      throw new IllegalStateException(exception);
    } finally {
      restoreContext(auth);
    }
  }

}
