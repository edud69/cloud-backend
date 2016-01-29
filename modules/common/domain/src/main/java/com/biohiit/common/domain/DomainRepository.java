package com.biohiit.common.domain;

public interface DomainRepository<DO extends DomainObject> {
	
	DO findById(final Long id);
	
	DO save(final DO domainObject);

}
