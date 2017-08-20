

package io.theshire.chat.server.configuration;

import io.theshire.chat.server.oauth2.resource.configuration.OAuth2ChatResourceServerConfigurer;
import io.theshire.common.server.configuration.BaseServerConfiguration;
import io.theshire.common.server.configuration.jpa.JpaConfigurer;
import io.theshire.common.websocket.configuration.WebsocketConfigurer;
import io.theshire.common.websocket.configuration.WebsocketOauth2ResourceIdentifier;
import io.theshire.common.websocket.endpoint.security.configuration.WebsocketSecurityConfigurer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@EnableAutoConfiguration(
    exclude = { ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class })
@Import(value = { JpaConfigurer.class, WebsocketConfigurer.class,
    OAuth2ChatResourceServerConfigurer.class, WebsocketSecurityConfigurer.class })
public class ChatServer extends BaseServerConfiguration {

 
  public static void main(String[] args) {
    System.setProperty("spring.config.name", "chat-server");
    SpringApplication.run(ChatServer.class, args);
  }

 
  @Bean
  public WebsocketOauth2ResourceIdentifier websocketOauth2ResourceIdentifier() {
    return new ChatServerWebsocketOauth2ResourceIdentifier();
  }

}