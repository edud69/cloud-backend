

package io.theshire.authorization.server.configuration.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class OAuth2WebMvcCorsMapper extends WebMvcConfigurerAdapter {

 
  @Value("${app.cloud.security.oauth.client.public.web.allowedOrigins}")
  private String allowedOrigins;


  @Override
  public void addCorsMappings(final CorsRegistry registry) {
    registry.addMapping("/login").allowedOrigins(allowedOrigins);
    registry.addMapping("/admin/login").allowedOrigins(allowedOrigins);
    registry.addMapping("/token/refresh").allowedOrigins(allowedOrigins);
  }

}
