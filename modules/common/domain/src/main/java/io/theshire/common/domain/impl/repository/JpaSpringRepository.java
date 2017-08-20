

package io.theshire.common.domain.impl.repository;

import io.theshire.common.domain.DomainObject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface JpaSpringRepository<E extends DomainObject>
    extends
      JpaRepository<E, Long>,
      JpaSpecificationExecutor<E> {

}
