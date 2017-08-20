

package io.theshire.account.server.configuration;

import io.theshire.account.server.oauth2.resource.configuration.OAuth2AccountResourceServerConfigurer;
import io.theshire.common.server.configuration.BaseServerConfiguration;
import io.theshire.common.server.configuration.elasticsearch.ElasticsearchConfigurer;
import io.theshire.common.server.configuration.jpa.JpaConfigurer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;


@EnableAutoConfiguration
@Import(value = { JpaConfigurer.class, ElasticsearchConfigurer.class,
    OAuth2AccountResourceServerConfigurer.class })
public class AccountServer extends BaseServerConfiguration {

 
  public static void main(String[] args) {
    System.setProperty("spring.config.name", "accounts-server");
    SpringApplication.run(AccountServer.class, args);
  }

}