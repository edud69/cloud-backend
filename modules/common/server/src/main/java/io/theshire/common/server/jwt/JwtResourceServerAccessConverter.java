

package io.theshire.common.server.jwt;

import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;

import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;


@Slf4j
public class JwtResourceServerAccessConverter extends JwtAccessConverter {

 
  @Autowired
  private LoadBalancerClient loadBalancerClient;

 
  @PostConstruct
  protected void initPublicKey() {
    final RestTemplate restTemplate = new RestTemplate();
    final String authService = OAuth2ResourceIdentifier.AuthService.getMicroserviceName();
    final ServiceInstance instance = loadBalancerClient.choose(authService);
    final String jwtPublicKey = restTemplate.getForObject(
        "http://" + instance.getHost() + ":" + instance.getPort() + "/oauth/token_key",
        String.class);
    log.info(
        "Obtained a public key for jwt verification from an authorization server, publicKey={}.",
        jwtPublicKey);
    final JSONObject json = new JSONObject(jwtPublicKey);
    final String publicKeyValue = json.getString("value");
    this.setVerifierKey(publicKeyValue);
  }

}
