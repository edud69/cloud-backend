

package io.theshire.gateway.server.impl.websocket;

import io.theshire.common.utils.web.url.UrlUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.util.Assert;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ UrlUtils.class, ContainerProvider.class })
public class CloudWebsocketClientTest {

 
  @Mock
  private WebSocketSession websocketSession;

 
  @Mock
  private LoadBalancerClient loadBalancerClient;

 
  @Mock
  private WebSocketContainer webSocketContainer;

 
  @Mock
  private ServiceInstance serviceInstance;

 
  @Mock
  private Session session;

 
  private CloudWebsocketClient classUnderTest;

 
  @Before
  public void setup() throws Exception {
    PowerMockito.mockStatic(ContainerProvider.class);
    PowerMockito.mockStatic(UrlUtils.class);
    PowerMockito.when(ContainerProvider.getWebSocketContainer()).thenReturn(webSocketContainer);
    PowerMockito.when(UrlUtils.extractParameterValueFromQueryUri("token=tokenValue", "token"))
        .thenReturn("tokenValue");

    final URI uri = new URI("wss://somedomain.com/ws/chat/endpoint?token=tokenValue");
    Mockito.when(websocketSession.getUri()).thenReturn(uri);
    Mockito.when(loadBalancerClient.choose("chat-service")).thenReturn(serviceInstance);
    Mockito.when(serviceInstance.getHost()).thenReturn("127.0.0.1");
    Mockito.when(serviceInstance.getPort()).thenReturn(1111);

    Mockito.when(webSocketContainer.connectToServer(Mockito.any(CloudWebsocketClient.class),
        Mockito.any(URI.class))).thenReturn(session);

    this.classUnderTest = new CloudWebsocketClient(websocketSession, loadBalancerClient);
  }

 
  @Test
  public void shouldDispatchOnMessageToTheSession() throws Exception {
    classUnderTest.onMessage("A message");
    Mockito.verify(websocketSession).sendMessage(new TextMessage("A message"));
  }

 
  @Test
  public void shouldHaveACloudSessionAttached() {
    Assert.notNull(classUnderTest.getCloudSession(),
        "classUnderTest.getCloudSession() cannot be null.");
  }

 
  @Test
  public void shouldNotCloseAlreadyClosedUnderlyingSession() throws Exception {
    Mockito.when(websocketSession.isOpen()).thenReturn(false);
    classUnderTest.onClose();
    Mockito.verify(websocketSession, Mockito.never()).close();
  }

 
  @Test
  public void shouldCloseUnderlyingSession() throws Exception {
    Mockito.when(websocketSession.isOpen()).thenReturn(true);
    classUnderTest.onClose();
    Mockito.verify(websocketSession).close();;
  }

}
