

package io.theshire.authorization.service.social.token;

import io.theshire.common.service.OutPort;

import org.springframework.security.oauth2.common.OAuth2AccessToken;


public interface SocialOAuth2TokenService {

 
  void get(final SocialOAuth2TokenGetInPort input, final OutPort<OAuth2AccessToken, ?> output);

}
