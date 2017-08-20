

package io.theshire.account.domain.impl;

import io.theshire.account.domain.Account;
import io.theshire.account.domain.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class AccountRepositoryImpl implements AccountRepository {

 
  @Autowired
  private AccountJpaRepository accountJpaRepository;


  public Account findById(final Long id) {
    return accountJpaRepository.findOne(id);
  }


  public Account findByUserId(final Long userId) {
    return accountJpaRepository.findByUserId(userId);
  }


  public List<Account> findAll() {
    return accountJpaRepository.findAll();
  }


  @Override
  public Account save(final Account domainObject) {
    return accountJpaRepository.save(domainObject);
  }


  @Override
  public void deleteByUserId(final Long userId) {
    accountJpaRepository.deleteByUserId(userId);
  }

}
