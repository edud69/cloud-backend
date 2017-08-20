

package io.theshire.common.websocket.endpoint.security.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;


@Configuration
public class WebsocketSecurityConfigurer extends AbstractSecurityWebSocketMessageBrokerConfigurer {

 
  @Value("${app.cloud.security.cors.websocket.allowOtherOrigin}")
  private String allowOtherOrigins;


  protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
    messages.simpSubscribeDestMatchers("/user/**", "/topic/**", "/queue/**", "/app/**")
        .access("@websocketSubscribeDestinationAccessRouter.checkAccess(authentication, message)");
    messages.simpMessageDestMatchers("/topic/**").denyAll().simpMessageDestMatchers("/queue/**")
        .denyAll().anyMessage().authenticated();
  }


  protected boolean sameOriginDisabled() {
    return Boolean.parseBoolean(allowOtherOrigins);
  }

}
