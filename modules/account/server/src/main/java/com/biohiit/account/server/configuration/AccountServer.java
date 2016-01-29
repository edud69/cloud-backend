package com.biohiit.account.server.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import com.biohiit.common.server.configuration.ElasticsearchConfig;
import com.biohiit.common.server.configuration.JPAConfig;

@EnableAutoConfiguration
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = "com.biohiit", excludeFilters = {
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.biohiit.*.configuration.*")
})
@Import(value = {JPAConfig.class, ElasticsearchConfig.class})
public class AccountServer {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "accounts-server");
        SpringApplication.run(AccountServer.class, args);
    }
   
}