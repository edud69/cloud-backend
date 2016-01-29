package com.biohiit.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.biohiit.account.service.AccountMsg;

@Service
public class AccountGatewayService {

    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;
    
    @Value("${app.cloud.service.account-service.serviceName}")
    private String serviceName;
    
    public AccountMsg getByNumber(String accountNumber) {
    	AccountMsg account = restTemplate.getForObject("http://" + serviceName + "/account/{number}", AccountMsg.class, accountNumber);
        return account;
    }
}