

package io.theshire.gateway.server.impl.websocket;

import io.theshire.common.utils.web.url.UrlUtils;

import lombok.Getter;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;


@ClientEndpoint
public class CloudWebsocketClient {

 
  private static final String TOKEN_PARAM = "token";

 
  private static final String GET_PARAM_CHAR = "?";

 
  private static final String WS_SCHEME = "ws://";

 
  private static final String WS_URI = "/ws/";

 
  private static final String SERVICE_ID_SUFFIX = "-service";

 
  private final WebSocketSession gatewaySession;

 

 
  @Getter
  private final Session cloudSession;

 
  public CloudWebsocketClient(final WebSocketSession gatewaySession,
      LoadBalancerClient loadBalancerClient) throws Exception {
    this.gatewaySession = gatewaySession;
    final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    final URI uri = gatewaySession.getUri();
    final String token = UrlUtils.extractParameterValueFromQueryUri(uri.getQuery(), TOKEN_PARAM);

    String path = uri.getPath();
    int idx = path.indexOf(WS_URI);
    if (idx == -1) {
      throw new IllegalArgumentException("Websocket query should have a **/ws/** path.");
    }

    idx = idx + WS_URI.length();
    path = path.substring(idx);

    idx = path.indexOf("/");
    final String serviceId = path.substring(0, idx) + SERVICE_ID_SUFFIX;

    path = path.substring(idx + 1, path.length());

    final ServiceInstance randInstance = loadBalancerClient.choose(serviceId);
    final StringBuilder url = new StringBuilder();

    url.append(WS_SCHEME);
    url.append(randInstance.getHost());
    url.append(":");
    url.append(randInstance.getPort());
    url.append(WS_URI);
    url.append(path);
    url.append(GET_PARAM_CHAR);
    url.append(TOKEN_PARAM);
    url.append("=");
    url.append(token);

    cloudSession = container.connectToServer(this, URI.create(url.toString()));
  }

 
  @OnMessage
  public void onMessage(String message) throws Exception {
    final TextMessage msg = new TextMessage(message);
    gatewaySession.sendMessage(msg);
  }

 
  @OnClose
  public void onClose() throws Exception {
    if (this.gatewaySession.isOpen()) {
      this.gatewaySession.close();
    }
  }

}
