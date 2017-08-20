

package io.theshire.common.server.configuration.elasticsearch;

import io.theshire.common.server.tenant.TenantResolver;

import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchPersistentEntity;
import org.springframework.data.util.TypeInformation;


public class ElasticsearchMultiTenantPersistentEntity<T>
    extends
      SimpleElasticsearchPersistentEntity<T> {

 
  public ElasticsearchMultiTenantPersistentEntity(TypeInformation<T> typeInformation) {
    super(typeInformation);
  }


  @Override
  public String getIndexName() {
    return TenantResolver.getTenantIdentifier();
  }

}
