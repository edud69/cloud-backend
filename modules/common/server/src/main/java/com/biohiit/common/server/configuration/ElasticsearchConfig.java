package com.biohiit.common.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.biohiit", includeFilters = {
		@ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*ElasticsearchRepository")
})
public class ElasticsearchConfig {
	
    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        //return new ElasticsearchTemplate(nodeBuilder().local(true).node().client()); TODO
    	return null;
    }
}
