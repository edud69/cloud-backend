package com.biohiit.common.service;

import com.biohiit.common.domain.DomainObject;

public abstract class MsgConverter<M extends CommMessage, DO extends DomainObject> {
	
	public DO toDomain(final M m) {
		return null;
	}
	
	
	public M fromDomain(final DO domainObject) {
		return null;
	}

}
