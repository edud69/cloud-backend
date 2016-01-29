package com.biohiit.common.domain.impl.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.biohiit.common.domain.DomainObject;

@NoRepositoryBean
public interface ElasticsearchSpringRepository<DO extends DomainObject> extends ElasticsearchRepository<DO, Long> {

}
