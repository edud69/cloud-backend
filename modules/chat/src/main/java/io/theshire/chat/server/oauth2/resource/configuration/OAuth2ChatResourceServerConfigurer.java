

package io.theshire.chat.server.oauth2.resource.configuration;

import io.theshire.common.server.configuration.oauth2.OAuth2ResourceServerConfigurer;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;


public class OAuth2ChatResourceServerConfigurer extends OAuth2ResourceServerConfigurer {

 
  @Value("${app.cloud.websockets.mq.connect.path}")
  private String wsChatConnectUri;


  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    super.configure(resources);
    resources.resourceId(OAuth2ResourceIdentifier.ChatService.getResourceId());
  }


  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers(wsChatConnectUri).permitAll();
    super.configure(http);
  }

}
