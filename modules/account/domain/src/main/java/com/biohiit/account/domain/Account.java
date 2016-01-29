package com.biohiit.account.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.data.elasticsearch.annotations.Document;

import com.biohiit.common.domain.DomainObject;

@Entity
@Table(name = "ACCOUNT")
@Document(indexName = "default")
public class Account extends DomainObject {
	

	private static final long serialVersionUID = 4065061504085900744L;
	
	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

}
