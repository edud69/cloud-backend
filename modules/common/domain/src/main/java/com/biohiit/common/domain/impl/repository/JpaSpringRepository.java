package com.biohiit.common.domain.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.biohiit.common.domain.DomainObject;

@NoRepositoryBean
public interface JpaSpringRepository<E extends DomainObject> extends JpaRepository<E, Long> {

}
