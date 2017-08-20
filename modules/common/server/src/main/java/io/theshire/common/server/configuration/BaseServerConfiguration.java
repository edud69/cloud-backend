

package io.theshire.common.server.configuration;

import io.theshire.common.utils.constants.PackageConstants;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableCircuitBreaker
@EnableHystrix
@EnableEurekaClient
@ComponentScan(basePackages = PackageConstants.ROOT_BASE_PACKAGE,
    excludeFilters = { @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = PackageConstants.CONFIGURATION_PACKAGES_EXCLUSION_REGEX) })
public abstract class BaseServerConfiguration {

 
  @LoadBalanced
  @Bean
  protected RestTemplate loadBalanced() {
    return new RestTemplate();
  }
}
