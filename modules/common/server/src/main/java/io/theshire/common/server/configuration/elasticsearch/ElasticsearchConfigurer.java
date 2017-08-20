

package io.theshire.common.server.configuration.elasticsearch;

import io.theshire.common.utils.constants.PackageConstants;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.PostConstruct;


@Configuration
@EnableElasticsearchRepositories(basePackages = PackageConstants.ROOT_BASE_PACKAGE,
    includeFilters = { @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = ElasticsearchConfigurer.ELASTICSEARCH_REPOSITORY_PATTERN) },
    elasticsearchTemplateRef = "elasticsearchMultiTenantTemplate")
@Import({ ElasticsearchSingleTenantConfigurer.class })


@Slf4j
public class ElasticsearchConfigurer {

 
  @Bean
  public ElasticsearchMultiTenantTemplate elasticsearchTemplate(Client client,
      ElasticsearchConverter converter) {
    try {
      return new ElasticsearchMultiTenantTemplate(client, converter);
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }

 

 
  @Getter
  private static volatile boolean active;

 
  protected static final String ELASTICSEARCH_REPOSITORY_PATTERN = ".*ElasticsearchRepository";

 
  @PostConstruct
  protected void configInitialized() {
    log.info("Elasticsearch configuration initialized.");
    log.info("Using {} regex for elasticsearch repository scan.", ELASTICSEARCH_REPOSITORY_PATTERN);
    setActive(true);
  }

 
  private static final void setActive(final boolean isActive) {
    active = isActive;
  }

}
