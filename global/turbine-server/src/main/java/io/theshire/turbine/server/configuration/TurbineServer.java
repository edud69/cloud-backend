

package io.theshire.turbine.server.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;


@SpringBootApplication
@EnableAutoConfiguration
@EnableEurekaClient
@EnableTurbineStream
public class TurbineServer {

 
  public static void main(String[] args) {
    System.setProperty("spring.config.name", "turbine-server");
    SpringApplication.run(TurbineServer.class, args);
  }

}
