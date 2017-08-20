

package io.theshire.authorization.endpoint.social.token;

import io.theshire.authorization.domain.social.SocialProvider;
import io.theshire.authorization.service.social.token.SocialOAuth2TokenService;
import io.theshire.common.endpoint.ManagedRestEndpoint;
import io.theshire.common.service.OutPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/social/token", produces = MediaType.APPLICATION_JSON_VALUE)
public class SocialOAuth2TokenEndpoint extends ManagedRestEndpoint {

 
  @Autowired
  private SocialOAuth2TokenService socialOAuth2TokenService;

 
  @ResponseBody
  @RequestMapping(value = "/{providerId}", method = RequestMethod.GET)
  public ResponseEntity<OAuth2AccessToken> httpGet(@PathVariable("providerId") String providerId) {
    final OutPort<OAuth2AccessToken, OAuth2AccessToken> output =
        OutPort.create(received -> received);
    socialOAuth2TokenService.get(() -> SocialProvider.fromId(providerId), output);
    return buildResponse(output.get());
  }

}
