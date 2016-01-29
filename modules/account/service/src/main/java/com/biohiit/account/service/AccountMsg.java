package com.biohiit.account.service;

import com.biohiit.common.service.CommMessage;

public class AccountMsg extends CommMessage {
	
	private String accountNumber;

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	

}
