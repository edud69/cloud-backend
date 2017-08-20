

package io.theshire.gateway.server.websocket.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class GatewayWebsocketConfigurer implements WebSocketConfigurer {

 
  @Autowired
  private WebSocketHandler websocketHandler;


  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(websocketHandler, "/wsconnect").setAllowedOrigins("*");
  }
}
