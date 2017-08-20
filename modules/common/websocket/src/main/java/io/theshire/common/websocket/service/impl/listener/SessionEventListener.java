

package io.theshire.common.websocket.service.impl.listener;

import io.theshire.common.websocket.endpoint.message.MessageHeaderExtractor;
import io.theshire.common.websocket.listener.WebsocketSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Optional;
import java.util.Set;


@Component
class SessionEventListener {

 
  @Autowired(required = false)
  private WebsocketSessionListener websocketSessionListener;

 
  @Autowired
  private SimpUserRegistry simpUserRegistry;

 
  @EventListener
  public void handleSessionUnsubscribeEvent(SessionUnsubscribeEvent event) {
    if (websocketSessionListener != null) {
      String destination = MessageHeaderExtractor.extractDestination(event.getMessage());
      if (destination == null) {
        String unsubId = MessageHeaderExtractor.extractUnsubscriptionId(event.getMessage());
        if (unsubId != null) {
          final SimpUser user = simpUserRegistry.getUser(event.getUser().getName());
          if (user != null) {
            final SimpSession s = user.getSession(
                MessageHeaderExtractor.extractSessionId(event.getMessage().getHeaders()));
            if (s != null) {
              destination = s.getSubscriptions().stream().filter(sub -> sub.getId().equals(unsubId))
                  .map(sub -> sub.getDestination()).findAny().orElse(null);
            }
          }
        }
      }

      if (destination != null) {
        websocketSessionListener.onSessionUnsubscribe(event.getUser(), event.getTimestamp(),
            destination);
      }
    }
  }

 
  @EventListener
  public void handleSessionConnectEvent(SessionConnectedEvent event) {
    if (websocketSessionListener != null) {
      websocketSessionListener.onSessionConnected(event.getUser(), event.getTimestamp());
    }
  }

 
  @EventListener
  public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
    final SimpUser user = simpUserRegistry.getUser(event.getUser().getName());
    if (user != null) {
      final Set<SimpSession> sessions = user.getSessions();
      if (!sessions.isEmpty()) {
        // gets the current session
        final Optional<SimpSession> s = sessions.stream()
            .filter(simpSession -> simpSession.getId().equals(event.getSessionId())).findAny();

        // unsubscribe from undisposed subscriptions for the current session
        if (s.isPresent()) {
          unsubscribeAllDestinations(event, s);
        }
      }

      if (websocketSessionListener != null) {
        websocketSessionListener.onSessionDisconnected(event.getUser(), event.getTimestamp());
      }
    }
  }

 
  private void unsubscribeAllDestinations(SessionDisconnectEvent event,
      final Optional<SimpSession> session) {
    session.get().getSubscriptions().forEach(sub -> {
      final StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.UNSUBSCRIBE);
      headers.setDestination(sub.getDestination());
      final Message<byte[]> msg =
          MessageBuilder.withPayload(new byte[1]).setHeaders(headers).build();

      this.handleSessionUnsubscribeEvent(
          new SessionUnsubscribeEvent(event.getSource(), msg, event.getUser()));
    });
  }

 
  @EventListener
  public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
    if (websocketSessionListener != null) {
      final String destination = MessageHeaderExtractor.extractDestination(event.getMessage());
      if (destination != null) {
        websocketSessionListener.onSessionSubscribe(event.getUser(), event.getTimestamp(),
            destination);
      }
    }
  }
}
