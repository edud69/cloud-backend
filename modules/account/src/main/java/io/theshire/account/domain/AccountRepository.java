

package io.theshire.account.domain;

import io.theshire.common.domain.DomainRepository;

import java.util.List;


public interface AccountRepository extends DomainRepository<Account> {

 
  Account findByUserId(final Long userId);

 
  List<Account> findAll();

 
  void deleteByUserId(final Long userId);

}
