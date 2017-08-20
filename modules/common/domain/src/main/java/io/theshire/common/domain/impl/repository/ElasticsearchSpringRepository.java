

package io.theshire.common.domain.impl.repository;

import io.theshire.common.domain.DomainObject;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface ElasticsearchSpringRepository<D extends DomainObject>
    extends
      ElasticsearchRepository<D, Long> {

 
  Class<D> getDomainClass();

 
  boolean isTenantAwareRepository();

}
