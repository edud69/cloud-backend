

package io.theshire.account.service.impl.lite;

import io.theshire.account.domain.Account;
import io.theshire.account.domain.AccountRepository;
import io.theshire.account.service.lite.AccountLiteGetInPort;
import io.theshire.account.service.lite.AccountLiteService;
import io.theshire.common.service.OutPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
class AccountLiteServiceImpl implements AccountLiteService {

 
  @Autowired
  private AccountRepository accountRepository;


  @Override
  public void get(final AccountLiteGetInPort input, final OutPort<Account, ?> output) {
    output.receive(accountRepository.findByUserId(input.getUserId()));
  }

}
