package com.biohiit.account.service.impl;

import org.springframework.stereotype.Service;

import com.biohiit.account.domain.Account;
import com.biohiit.account.service.AccountMsg;
import com.biohiit.common.service.MsgConverter;

@Service
public class AccountMsgConverter extends MsgConverter<AccountMsg, Account> {
	
	public AccountMsg toMsg(final Account account) {
		AccountMsg accountMsg = new AccountMsg();
		if(account != null) {
			accountMsg.setAccountNumber(account.getAccountNumber());
		}
		return accountMsg;
	}
	
	public Account fromMsg(final AccountMsg accountMsg) {
		Account account = new Account(); 
		if(accountMsg != null) {
			account.setAccountNumber(accountMsg.getAccountNumber());
		}
		return account;
	}

}
