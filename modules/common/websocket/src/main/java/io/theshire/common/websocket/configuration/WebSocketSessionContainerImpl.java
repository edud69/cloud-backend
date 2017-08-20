

package io.theshire.common.websocket.configuration;

import io.theshire.common.websocket.service.session.WebSocketSessionContainer;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
class WebSocketSessionContainerImpl implements WebSocketSessionContainer {

 
  private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

 
  public void add(final WebSocketSession session) {
    sessions.put(session.getId(), session);
  }

 
  public void remove(final WebSocketSession session) {
    sessions.remove(session.getId());
  }


  @Override
  public WebSocketSession getSession(String id) {
    return this.sessions.get(id);
  }

}
