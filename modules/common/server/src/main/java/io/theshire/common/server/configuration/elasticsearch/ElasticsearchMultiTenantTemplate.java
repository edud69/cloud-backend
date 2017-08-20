

package io.theshire.common.server.configuration.elasticsearch;

import org.elasticsearch.client.Client;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;


public class ElasticsearchMultiTenantTemplate extends ElasticsearchTemplate {

 
  public ElasticsearchMultiTenantTemplate(Client client) {
    this(client, new MappingElasticsearchConverter(new ElasticsearchMultiTenantMappingContext()));
  }

 
  public ElasticsearchMultiTenantTemplate(Client client, EntityMapper entityMapper) {
    this(client, new MappingElasticsearchConverter(new ElasticsearchMultiTenantMappingContext()),
        entityMapper);
  }

 
  public ElasticsearchMultiTenantTemplate(Client client,
      ElasticsearchConverter elasticsearchConverter, EntityMapper entityMapper) {
    super(client, elasticsearchConverter,
        new DefaultResultMapper(elasticsearchConverter.getMappingContext(), entityMapper));
  }

 
  public ElasticsearchMultiTenantTemplate(Client client, ResultsMapper resultsMapper) {
    super(client, new MappingElasticsearchConverter(new ElasticsearchMultiTenantMappingContext()),
        resultsMapper);
  }

 
  public ElasticsearchMultiTenantTemplate(Client client,
      ElasticsearchConverter elasticsearchConverter) {
    super(client, elasticsearchConverter,
        new DefaultResultMapper(elasticsearchConverter.getMappingContext()));
  }

}
