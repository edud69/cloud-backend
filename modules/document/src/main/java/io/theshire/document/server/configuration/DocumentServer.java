

package io.theshire.document.server.configuration;

import io.theshire.common.server.configuration.BaseServerConfiguration;
import io.theshire.common.server.configuration.elasticsearch.ElasticsearchConfigurer;
import io.theshire.common.server.configuration.jpa.JpaConfigurer;
import io.theshire.document.server.oauth2.resource.configuration.OAuth2DocumentResourceServerConfigurer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;


@EnableAutoConfiguration
@Import(value = { JpaConfigurer.class, ElasticsearchConfigurer.class,
    OAuth2DocumentResourceServerConfigurer.class })
public class DocumentServer extends BaseServerConfiguration {

 
  public static void main(String[] args) {
    System.setProperty("spring.config.name", "document-server");
    SpringApplication.run(DocumentServer.class, args);
  }

}