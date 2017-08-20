

package io.theshire.authorization.service.social.oauth2;

import io.theshire.authorization.domain.social.SocialProvider;


public interface OAuth2Providers {

 
  OAuth2ConnectionFactoryConfiguration get(final SocialProvider provider);

}
