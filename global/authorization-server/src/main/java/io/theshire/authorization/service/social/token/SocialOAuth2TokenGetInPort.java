

package io.theshire.authorization.service.social.token;

import io.theshire.authorization.domain.social.SocialProvider;


@FunctionalInterface
public interface SocialOAuth2TokenGetInPort {

 
  SocialProvider getProvider();

}
