

package io.theshire.common.websocket.configuration;

import io.theshire.common.utils.authentication.OAuth2AuthenticationManagerBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;


@Configuration
public class WebsocketAuthenticationManager {

  @Autowired
  private WebsocketOauth2ResourceIdentifier websocketOauth2ResourceIdentifier;

 
  @Bean
  protected OAuth2AuthenticationManager
      initAuthenticationManager(final JwtAccessTokenConverter jwtTokenEnhancer) {
    return new OAuth2AuthenticationManagerBuilder()
        .resource(websocketOauth2ResourceIdentifier.getOAuth2ResourceIdentifier())
        .jwtTokenEnhancer(jwtTokenEnhancer).build();
  }
}
