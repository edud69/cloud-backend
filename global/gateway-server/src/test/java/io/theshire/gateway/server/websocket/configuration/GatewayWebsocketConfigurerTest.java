

package io.theshire.gateway.server.websocket.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@RunWith(PowerMockRunner.class)
public class GatewayWebsocketConfigurerTest {

 
  @Mock
  private WebSocketHandler websocketHandler;

 
  @InjectMocks
  private GatewayWebsocketConfigurer classUnderTest = new GatewayWebsocketConfigurer();

 
  @Test
  public void ShouldHaveAWebsocketHandler() {
    final WebSocketHandlerRegistry registry = Mockito.mock(WebSocketHandlerRegistry.class);
    final WebSocketHandlerRegistration registration =
        Mockito.mock(WebSocketHandlerRegistration.class);

    Mockito.when(registry.addHandler(websocketHandler, "/wsconnect")).thenReturn(registration);

    classUnderTest.registerWebSocketHandlers(registry);

    Mockito.verify(registry).addHandler(websocketHandler, "/wsconnect");
    Mockito.verify(registration).setAllowedOrigins("*");
  }

}
