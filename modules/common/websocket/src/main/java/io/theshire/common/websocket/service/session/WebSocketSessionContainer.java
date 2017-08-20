

package io.theshire.common.websocket.service.session;

import org.springframework.web.socket.WebSocketSession;


public interface WebSocketSessionContainer {

 
  WebSocketSession getSession(final String id);

}
