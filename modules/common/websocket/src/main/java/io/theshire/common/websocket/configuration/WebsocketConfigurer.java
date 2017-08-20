

package io.theshire.common.websocket.configuration;

import io.theshire.common.websocket.interceptor.SecuredChannelInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;

//Topics
//In JMS a Topic implements publish and subscribe semantics. 
// When you publish a message it goes to all the subscribers who are interested - 
// so zero to many subscribers will receive a 
// copy of the message. Only subscribers who had an active subscription 
// at the time the broker receives 
// the message will get a 
// copy of the message.
//Queues
//A JMS Queue implements load balancer semantics. A single message 
// will be received by exactly one consumer. 
// If there are no consumers available at the time the message is sent it will be kept 
// until a consumer is available that can process the message. If a consumer receives a 
// message and does not acknowledge it before closing then the message will be 
// redelivered to another consumer. A queue can have many consumers with 
// messages load balanced across the available consumers.

// So Queues implement a reliable load balancer in JMS.
@Configuration
@EnableWebSocketMessageBroker
@Import({ WebsocketAuthenticationManager.class, WebSocketSessionContainerImpl.class })
public class WebsocketConfigurer implements WebSocketMessageBrokerConfigurer {

 
  @Value("${app.cloud.websockets.mq.userelay}")
  private String useStompRelay;

 
  @Value("${app.cloud.websockets.mq.relay.login}")
  private String relayLogin;

 
  @Value("${app.cloud.websockets.mq.relay.passcode}")
  private String relayPasscode;

 
  @Value("${app.cloud.websockets.mq.relay.host}")
  private String relayHost;

 
  @Value("${app.cloud.websockets.mq.relay.port}")
  private String relayPort;

 
  @Value("${app.cloud.websockets.mq.connect.path}")
  private String connectPath;

 
  @Value("${app.cloud.security.cors.allowedOrigin}")
  private String allowedOrigin;

 
  @Autowired
  private HandshakeInterceptor handshakeInterceptor;

 
  @Autowired
  private WebSocketSessionContainerImpl sessionContainer;


  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint(connectPath).addInterceptors(handshakeInterceptor)
        .setAllowedOrigins(allowedOrigin);
  }


  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // all calls to /app means that the query to the broker will be processed with the java
    // application before going to the message broker (security purposes)
    registry.setApplicationDestinationPrefixes("/app");
    registry.setUserDestinationPrefix("/user");

    if ("true".equals(useStompRelay)) {
      registry.enableStompBrokerRelay("/queue", "/topic").setClientLogin(relayLogin)
          .setSystemLogin(relayLogin).setClientPasscode(relayPasscode)
          .setSystemPasscode(relayPasscode).setRelayHost(relayHost)
          .setRelayPort(Integer.parseInt(relayPort));
    } else {
      registry.enableSimpleBroker("/queue", "/topic");
    }
  }


  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    registry.setDecoratorFactories(
        wsHandler -> new CustomWebsocketHandlerDecorator(sessionContainer, wsHandler));
  }


  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.setInterceptors(new SecuredChannelInterceptor());
  }


  @Override
  public void configureClientOutboundChannel(ChannelRegistration registration) {
    registration.setInterceptors(new SecuredChannelInterceptor());
  }


  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
  }


  @Override
  public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
  }


  @Override
  public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    return true;
  }

}
