

package io.theshire.common.domain;


public interface DomainRepository<D extends DomainObject> {

 
  D findById(final Long id);

 
  D save(final D domainObject);

}
