

package io.theshire.common.server.configuration.elasticsearch;

import io.theshire.common.utils.constants.PackageConstants;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


@EnableElasticsearchRepositories(basePackages = PackageConstants.ROOT_BASE_PACKAGE,
    includeFilters = { @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = ElasticsearchSingleTenantConfigurer.ELASTICSEARCH_SINGLE_TENANT_REPO_PATTERN) })
public class ElasticsearchSingleTenantConfigurer {

 
  protected static final String ELASTICSEARCH_SINGLE_TENANT_REPO_PATTERN =
      ".*ElasticsearchSingleTenantRepository";

}