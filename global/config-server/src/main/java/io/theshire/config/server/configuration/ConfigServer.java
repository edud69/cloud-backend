

package io.theshire.config.server.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.security.oauth2.proxy.OAuth2ProxyAutoConfiguration;


@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class,
    DataSourceAutoConfiguration.class, ElasticsearchAutoConfiguration.class,
    ElasticsearchDataAutoConfiguration.class, OAuth2AutoConfiguration.class,
    OAuth2ProxyAutoConfiguration.class, SecurityAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class, RabbitAutoConfiguration.class })
public class ConfigServer {

 
  public static void main(String[] args) {
    System.setProperty("spring.config.name", "config-server");
    SpringApplication.run(ConfigServer.class, args);
  }
}
