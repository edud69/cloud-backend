package com.biohiit.gateway.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.biohiit.account.service.AccountMsg;
import com.biohiit.common.endpoint.ManagedRestResource;
import com.biohiit.gateway.service.AccountGatewayService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

//TODO create a rest-common project, with @RestExceptionHandler
@RestController
@RequestMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountRestGatewayResource extends ManagedRestResource {
	
	//TODO secured annotations here
	
	@Autowired
	private AccountGatewayService accountGatewayService;
	
	@HystrixCommand(fallbackMethod = "fallbackHttpGet")
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/{accountNumber}")
	public AccountMsg httpGet(@PathVariable("accountNumber") final String accountNumber) {
		return accountGatewayService.getByNumber(accountNumber);
	}
	
	@ResponseBody
    public Object fallbackHttpGet(final String accountNumber) {
        //do stuff that might fail
    	return null;
    }

}

