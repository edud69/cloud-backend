

package io.theshire.account.service.impl;

import io.theshire.account.domain.Account;
import io.theshire.account.domain.AccountRepository;
import io.theshire.account.service.AccountCreateInPort;
import io.theshire.account.service.AccountDeleteInPort;
import io.theshire.account.service.AccountGetInPort;
import io.theshire.account.service.AccountService;
import io.theshire.account.service.AccountUpdateInPort;
import io.theshire.common.service.OutPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AccountServiceImpl implements AccountService {

 
  @Autowired
  private AccountRepository accountRepository;


  @Override
  public void getByUserId(final AccountGetInPort input, OutPort<Account, ?> output) {
    output.receive(accountRepository.findByUserId(input.getUserId()));
  }


  @Override
  public void update(final AccountUpdateInPort input, OutPort<Account, ?> output) {
    final Account inputAccount = input.getAccount();
    final Account account = accountRepository.findByUserId(inputAccount.getUserId());
    final Account updated = accountRepository
        .save(account.update(inputAccount.getFirstName(), inputAccount.getLastName(),
            inputAccount.getGender(), inputAccount.getBirthday(), inputAccount.getAvatarUrl()));
    output.receive(updated);
  }


  @Override
  public void deleteByUserId(final AccountDeleteInPort input, OutPort<Boolean, ?> output) {
    accountRepository.deleteByUserId(input.getUserId());
    output.receive(true);
  }


  @Override
  public void create(AccountCreateInPort input, OutPort<Account, ?> output) {
    output.receive(accountRepository.save(input.getAccount()));
  }

}
