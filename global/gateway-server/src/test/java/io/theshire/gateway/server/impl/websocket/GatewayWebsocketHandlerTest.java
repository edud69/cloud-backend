

package io.theshire.gateway.server.impl.websocket;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ CloudWebsocketClient.class, GatewayWebsocketHandler.class })
public class GatewayWebsocketHandlerTest {

 
  @Mock
  private LoadBalancerClient loadBalancerClient;

 
  @Mock
  private WebSocketSession session;

 
  @InjectMocks
  private GatewayWebsocketHandler classUnderTest;

 
  @Test
  public void shouldCreateAnEntryWhenConnectionEstablished() throws Exception {
    final CloudWebsocketClient mockedCloudSession = PowerMockito.mock(CloudWebsocketClient.class);
    PowerMockito.whenNew(CloudWebsocketClient.class).withAnyArguments()
        .thenReturn(mockedCloudSession);

    PowerMockito.when(session.getId()).thenReturn("aCustomId");

    classUnderTest.afterConnectionEstablished(session);

    @SuppressWarnings("unchecked")
    final Map<String, CloudWebsocketClient> cloudWebsocketConnections =
        (Map<String, CloudWebsocketClient>) ReflectionTestUtils.getField(classUnderTest,
            "cloudWebsocketConnections");

    Assert.assertEquals(mockedCloudSession, cloudWebsocketConnections.get("aCustomId"));
  }

 
  @Test
  public void shouldDispatchTheMessage() throws Exception {
    @SuppressWarnings("unchecked")
    final Map<String, CloudWebsocketClient> cloudWebsocketConnections =
        (Map<String, CloudWebsocketClient>) ReflectionTestUtils.getField(classUnderTest,
            "cloudWebsocketConnections");

    final CloudWebsocketClient mockedCloudSession = PowerMockito.mock(CloudWebsocketClient.class);
    cloudWebsocketConnections.put("aCustomId", mockedCloudSession);

    final Session mockedSession = PowerMockito.mock(Session.class);
    final Basic mockedBasic = PowerMockito.mock(Basic.class);
    PowerMockito.when(mockedCloudSession.getCloudSession()).thenReturn(mockedSession);
    PowerMockito.when(mockedSession.getBasicRemote()).thenReturn(mockedBasic);

    Mockito.when(session.getId()).thenReturn("aCustomId");

    final TextMessage message = new TextMessage("some message");
    classUnderTest.handleMessage(session, message);

    Mockito.verify(mockedBasic).sendText("some message");
  }

 
  @Test
  public void shouldDisposeResourcesOnClose() throws Exception {
    @SuppressWarnings("unchecked")
    final Map<String, CloudWebsocketClient> cloudWebsocketConnections =
        (Map<String, CloudWebsocketClient>) ReflectionTestUtils.getField(classUnderTest,
            "cloudWebsocketConnections");

    final CloudWebsocketClient mockedCloudSession = PowerMockito.mock(CloudWebsocketClient.class);
    cloudWebsocketConnections.put("aCustomId", mockedCloudSession);

    final WebSocketSession session = PowerMockito.mock(WebSocketSession.class);
    Mockito.when(session.getId()).thenReturn("aCustomId");

    final Session mockedSession = PowerMockito.mock(Session.class);
    Mockito.when(mockedCloudSession.getCloudSession()).thenReturn(mockedSession);
    Mockito.when(mockedSession.isOpen()).thenReturn(true);

    classUnderTest.afterConnectionClosed(session, null);

    Mockito.verify(mockedSession).close();
    Assert.assertTrue(cloudWebsocketConnections.isEmpty());
  }

 
  @Test
  public void shouldNotSupportPartialMessages() {
    Assert.assertFalse(classUnderTest.supportsPartialMessages());
  }

}
