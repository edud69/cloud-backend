

package io.theshire.admin.server.configuration;

import io.theshire.admin.server.oauth2.resource.configuration.OAuth2AdminResourceServerConfigurer;
import io.theshire.common.server.configuration.BaseServerConfiguration;
import io.theshire.common.server.configuration.jpa.JpaConfigurer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Import;


@EnableAutoConfiguration(
    exclude = { ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class })
@EnableHystrixDashboard
@Import(value = { JpaConfigurer.class, OAuth2AdminResourceServerConfigurer.class })
public class AdminServer extends BaseServerConfiguration {

 
  public static void main(String[] args) {
    System.setProperty("spring.config.name", "admin-server");
    SpringApplication.run(AdminServer.class, args);
  }

 
  @Autowired
  public void configureRabbitMqTemplate(final RabbitTemplate messagingTemplate) {
    messagingTemplate.setMessageConverter(new JsonMessageConverter());
  }
}