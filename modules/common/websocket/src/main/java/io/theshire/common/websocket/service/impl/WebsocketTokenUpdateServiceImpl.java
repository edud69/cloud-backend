

package io.theshire.common.websocket.service.impl;

import io.theshire.common.websocket.endpoint.message.MessageHeaderExtractor;
import io.theshire.common.websocket.service.WebsocketTokenUpdateInPort;
import io.theshire.common.websocket.service.WebsocketTokenUpdateService;
import io.theshire.common.websocket.service.session.WebSocketSessionContainer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.function.Consumer;






@Slf4j
@Service
class WebsocketTokenUpdateServiceImpl implements WebsocketTokenUpdateService {

 
  @Autowired
  private OAuth2AuthenticationManager authenticationManager;

 
  @Autowired
  private WebSocketSessionContainer webSocketSessionContainer;

 

  @Override
  public void process(final WebsocketTokenUpdateInPort input, Consumer<LocalDateTime> output) {
    updateToken(input.getToken(), input.getHeaders());
    output.accept(LocalDateTime.now(Clock.systemUTC()));
  }

 
  private void updateToken(final String newToken, final MessageHeaders headers) {
    if (newToken == null) {
      throw new SecurityException("Token cannot be null.");
    }

    final Authentication validatedAuth = authenticate(newToken);
    updateWebsocketSessionAuthentication(headers, validatedAuth);

    log.debug("Token has been refreshed for user {}.", validatedAuth.getName());
  }

 
  private void updateWebsocketSessionAuthentication(final MessageHeaders headers,
      final Authentication validatedAuth) {
    final String sessionId = MessageHeaderExtractor.extractSessionId(headers);
    final WebSocketSession session = webSocketSessionContainer.getSession(sessionId);
    final StandardWebSocketSession stdWebSocketSession = ((StandardWebSocketSession) session);
    try {
      final Field f = stdWebSocketSession.getClass().getDeclaredField("user");
      final boolean isAccessible = f.isAccessible();

      try {
        if (!isAccessible) {
          f.setAccessible(true);
        }

        f.set(stdWebSocketSession, validatedAuth);
      } finally {
        f.setAccessible(isAccessible);
      }
    } catch (Exception exc) {
      throw new RuntimeException(exc);
    }
  }

 
  private Authentication authenticate(final String newToken) {
    final PreAuthenticatedAuthenticationToken authentication =
        new PreAuthenticatedAuthenticationToken(newToken, "");
    final Authentication validatedAuth = authenticationManager.authenticate(authentication);
    SecurityContextHolder.getContext().setAuthentication(validatedAuth);
    return validatedAuth;
  }

}
