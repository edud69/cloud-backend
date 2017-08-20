

package io.theshire.sr.server.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistrationServer {

 
  public static void main(String[] args) {
    System.setProperty("spring.config.name", "service-registry-server");
    SpringApplication.run(ServiceRegistrationServer.class, args);
  }
}
