

package io.theshire.account.service;

import io.theshire.account.domain.Account;


@FunctionalInterface
public interface AccountUpdateInPort {

 
  Account getAccount();

}
