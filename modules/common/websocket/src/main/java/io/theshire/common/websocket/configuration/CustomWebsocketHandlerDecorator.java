

package io.theshire.common.websocket.configuration;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;


class CustomWebsocketHandlerDecorator extends WebSocketHandlerDecorator {

 
  private final WebSocketSessionContainerImpl sessionContainer;

 
  public CustomWebsocketHandlerDecorator(WebSocketSessionContainerImpl sessionContainer,
      WebSocketHandler delegate) {
    super(delegate);
    this.sessionContainer = sessionContainer;
  }


  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    sessionContainer.add(session);
  }


  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
      throws Exception {
    super.afterConnectionClosed(session, closeStatus);
    sessionContainer.remove(session);
  }

}
