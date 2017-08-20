

package io.theshire.gateway.server.configuration;

import io.theshire.common.utils.constants.PackageConstants;
import io.theshire.gateway.server.security.configuration.WebSecurityConfigurer;
import io.theshire.gateway.server.websocket.configuration.GatewayWebsocketConfigurer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.security.oauth2.proxy.OAuth2ProxyAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@EnableAutoConfiguration(
    exclude = { HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
        XADataSourceAutoConfiguration.class, JtaAutoConfiguration.class,
        ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class,
        OAuth2AutoConfiguration.class, OAuth2ProxyAutoConfiguration.class,
        SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@EnableCircuitBreaker
@EnableHystrix
@EnableZuulProxy
@ComponentScan(basePackages = PackageConstants.ROOT_BASE_PACKAGE + ".gateway",
    excludeFilters = { @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = PackageConstants.CONFIGURATION_PACKAGES_EXCLUSION_REGEX) })
@Import({ WebSecurityConfigurer.class, GatewayWebsocketConfigurer.class })
public class GatewayServer {

 
  public static void main(String[] args) {
    System.setProperty("spring.config.name", "rest-api-server");
    SpringApplication.run(GatewayServer.class, args);
  }
}
