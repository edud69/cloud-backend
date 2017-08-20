

package io.theshire.authorization.endpoint.web.login;

import io.theshire.common.endpoint.ManagedRestEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


public abstract class AbstractWebLoginEndpoint extends ManagedRestEndpoint {

 
  @Autowired
  private ClientDetailsService clientDetailsService;

 
  @Autowired
  private TokenEndpoint tokenEndpoint;

 
  protected ResponseEntity<OAuth2AccessToken> httpPost(final HttpServletRequest request,
      final String clientId) throws HttpRequestMethodNotSupportedException {
    final String[] creds = getCredentials(request);
    final Map<String, String> parameters = new HashMap<>();
    parameters.put("username", creds[0]);
    parameters.put("password", creds[1]);
    parameters.put("grant_type", "password");
    final ClientDetails clDetails = clientDetailsService.loadClientByClientId(clientId);
    final UsernamePasswordAuthenticationToken client = new UsernamePasswordAuthenticationToken(
        clDetails.getClientId(), clDetails.getClientSecret(), clDetails.getAuthorities());
    client.setDetails(clDetails);
    return tokenEndpoint.postAccessToken(client, parameters);
  }

 
  protected String[] getCredentials(final HttpServletRequest request) {
    String[] creds = null;
    boolean validBasicAuth = false;

    final String authorization = request.getHeader("Authorization");
    if (authorization != null && authorization.startsWith("Basic")) {
      // Authorization: Basic base64credentials
      String base64Credentials = authorization.substring("Basic".length()).trim();
      String credentials =
          new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
      // credentials = username:password
      creds = credentials.split(":", 2);
      if (creds.length == 2) {
        validBasicAuth = true;
      }
    }

    if (!validBasicAuth) {
      throw new IllegalArgumentException("A valid Basic HTTP authorization header is needed.");
    }
    return creds;
  }

}
