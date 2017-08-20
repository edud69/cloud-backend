

package io.theshire.common.websocket.interceptor;

import io.theshire.common.utils.web.url.UrlUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.WebUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;


@Slf4j
@Component
public class OAuth2HandshakeInterceptor implements HandshakeInterceptor {

 
  private static final String TOKEN_PARAM = "token";

 
  @Autowired
  private OAuth2AuthenticationManager authenticationManager;

 
  @Value("${app.cloud.security.cors.allowedOrigin}")
  private String allowedOrigins;


  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

    ensureTrustedOrigins(request);
    authenticate(request);

    return true;
  }

 
  private void ensureTrustedOrigins(final ServerHttpRequest request) {
    final Collection<String> validOrigins = new HashSet<>(1);
    validOrigins.add(allowedOrigins);

    if (!WebUtils.isValidOrigin(request, validOrigins)) {
      throw new SecurityException(String.format(
          "Websocket comes from a different origin than what is allowed (%s)", allowedOrigins));
    }
  }

 
  private void authenticate(ServerHttpRequest request) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
      final String query = request.getURI().getQuery();
      final String token = UrlUtils.extractParameterValueFromQueryUri(query, TOKEN_PARAM);
      final PreAuthenticatedAuthenticationToken authentication =
          new PreAuthenticatedAuthenticationToken(token, "");
      final Authentication validatedAuth = authenticationManager.authenticate(authentication);
      SecurityContextHolder.getContext().setAuthentication(validatedAuth);
      auth = SecurityContextHolder.getContext().getAuthentication();
    }

    log.trace("websocket connection initialized. auth={}", auth);
  }


  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception ex) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    log.trace("after handshake. auth={}.", auth);
  }
}
