package com.biohiit.account.endpoint.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.biohiit.account.service.AccountMsg;
import com.biohiit.account.service.AccountService;
import com.biohiit.common.endpoint.ManagedRestResource;


@RestController
@RequestMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountRestResource extends ManagedRestResource {
	
	@Autowired
	private AccountService accountService;
	
	//TODO secured annotations here
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/{accountNumber}")
	public AccountMsg httpGet(@PathVariable("accountNumber") final String accountNumber) {
		return accountService.getByNumber(accountNumber);
	}

}
