

package io.theshire.gateway.server.impl.websocket;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;


@Slf4j
@Component
class GatewayWebsocketHandler implements WebSocketHandler {

 
  private final Map<String, CloudWebsocketClient> cloudWebsocketConnections =
      new ConcurrentHashMap<>();

 
  @Autowired
  private LoadBalancerClient loadBalancerClient;


  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    log.debug("Creating a new gateway websocket connection for session={}.", session);
    final CloudWebsocketClient cloudClient = new CloudWebsocketClient(session, loadBalancerClient);
    cloudWebsocketConnections.put(session.getId(), cloudClient);
  }


  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
      throws Exception {
    final Session cloudSession = cloudWebsocketConnections.get(session.getId()).getCloudSession();
    cloudSession.getBasicRemote().sendText(((TextMessage) message).getPayload());
  }


  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    log.debug("Error occurred for session={}, Message: {}.", session, exception.getMessage());
  }


  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
      throws Exception {
    final CloudWebsocketClient cloudClient = cloudWebsocketConnections.remove(session.getId());
    if (cloudClient != null) {
      final Session s = cloudClient.getCloudSession();
      if (s != null && s.isOpen()) {
        s.close();
      }
    }
  }


  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

}
