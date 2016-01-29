package com.biohiit.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biohiit.account.domain.Account;
import com.biohiit.account.domain.AccountRepository;
import com.biohiit.account.service.AccountMsg;
import com.biohiit.account.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private AccountMsgConverter accountMsgConverter;

	@Transactional
    public AccountMsg getByNumber(final String accountNumber) {
    	final Account account = accountMsgConverter.fromMsg(new AccountMsg());
        return accountMsgConverter.toMsg(account);
    }
}
