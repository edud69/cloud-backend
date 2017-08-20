

package io.theshire.authorization.server.configuration;

import io.theshire.authorization.server.configuration.authentication.AuthenticationManagerConfigurer;
import io.theshire.authorization.server.configuration.oauth2.OAuth2AuthorizationServerConfigurer;
import io.theshire.common.server.configuration.BaseServerConfiguration;
import io.theshire.common.server.configuration.jpa.JpaConfigurer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.Import;


@EnableAutoConfiguration(
    exclude = { ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class })
@Import({ OAuth2AuthorizationServerConfigurer.class, AuthenticationManagerConfigurer.class,
    JpaConfigurer.class })
public class AuthorizationServer extends BaseServerConfiguration {

 
  public static void main(String[] args) {
    System.setProperty("spring.config.name", "authorization-server");
    SpringApplication.run(AuthorizationServer.class, args);
  }

}
