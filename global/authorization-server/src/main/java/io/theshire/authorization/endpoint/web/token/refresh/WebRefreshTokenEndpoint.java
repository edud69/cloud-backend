

package io.theshire.authorization.endpoint.web.token.refresh;

import io.theshire.common.endpoint.ManagedRestEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/token/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
public class WebRefreshTokenEndpoint extends ManagedRestEndpoint {

 
  @Value("${app.cloud.security.oauth.client.public.web.id}")
  private String publicWebClientId;

 
  @Autowired
  private ClientDetailsService clientDetailsService;

 
  @Autowired
  private TokenEndpoint tokenEndpoint;

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<OAuth2AccessToken> httpRefreshPost(
      @RequestParam("refresh_token") final String refreshToken)
      throws HttpRequestMethodNotSupportedException {
    final Map<String, String> parameters = new HashMap<>();
    parameters.put("grant_type", "refresh_token");
    parameters.put("refresh_token", refreshToken);
    final ClientDetails clDetails = clientDetailsService.loadClientByClientId(publicWebClientId);
    final UsernamePasswordAuthenticationToken client = new UsernamePasswordAuthenticationToken(
        clDetails.getClientId(), clDetails.getClientSecret(), clDetails.getAuthorities());
    client.setDetails(clDetails);
    return tokenEndpoint.postAccessToken(client, parameters);
  }

}
