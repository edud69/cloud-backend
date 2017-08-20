

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
@RequestMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
public class WebLoginEndpoint extends AbstractWebLoginEndpoint {

 
  @Value("${app.cloud.security.oauth.client.public.web.id}")
  private String publicWebClientId;

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<OAuth2TokenMessage> httpPost(final HttpServletRequest request)
      throws HttpRequestMethodNotSupportedException {
    final ResponseEntity<OAuth2AccessToken> response = super.httpPost(request, publicWebClientId);

    final OAuth2TokenMessage transportMessage = new OAuth2TokenMessage();
    transportMessage.setAccessToken(response.getBody().getValue());
    transportMessage.setRefreshToken(response.getBody().getRefreshToken().getValue());

    return new ResponseEntity<OAuth2TokenMessage>(transportMessage, response.getStatusCode());
  }

}
