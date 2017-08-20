

package io.theshire.authorization.endpoint.web.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(value = "/admin/login", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminWebLoginEndpoint extends AbstractWebLoginEndpoint {

 
  @Value("${app.cloud.security.oauth.client.internal.id}")
  private String internalWebClientId;

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<OAuth2AccessToken> httpPost(final HttpServletRequest request)
      throws HttpRequestMethodNotSupportedException {
    return super.httpPost(request, internalWebClientId);
  }

}
