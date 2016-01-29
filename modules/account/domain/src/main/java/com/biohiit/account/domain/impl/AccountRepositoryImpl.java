package com.biohiit.account.domain.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biohiit.account.domain.Account;
import com.biohiit.account.domain.AccountRepository;
import com.biohiit.common.util.transaction.TransactionUtils;

@Service
@Transactional
public class AccountRepositoryImpl implements AccountRepository {
	
	@Autowired
	private AccountJpaRepository accountJpaRepository;
	
	@Autowired
	private AccountElasticsearchRepository accountElasticsearchRepository;

	public Account findById(final Long id) {
		return accountJpaRepository.findOne(id);
	}

	public Account save(final Account domainObject) {
		TransactionUtils.runAfterCommit(() -> {
			accountElasticsearchRepository.index(domainObject);
		});

		return accountJpaRepository.save(domainObject);
	}

}
