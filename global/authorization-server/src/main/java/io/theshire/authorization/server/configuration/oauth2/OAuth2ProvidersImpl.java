

package io.theshire.authorization.server.configuration.oauth2;

import io.theshire.authorization.domain.social.SocialProvider;
import io.theshire.authorization.service.social.oauth2.OAuth2ConnectionFactoryConfiguration;
import io.theshire.authorization.service.social.oauth2.OAuth2Providers;

import java.util.Collections;
import java.util.Map;


class OAuth2ProvidersImpl implements OAuth2Providers {

 
  private final Map<String, OAuth2ConnectionFactoryConfiguration> providers;

 
  protected OAuth2ProvidersImpl(
      final Map<String, OAuth2ConnectionFactoryConfiguration> providerConfigs) {
    this.providers = Collections.unmodifiableMap(providerConfigs);
  }

 
  @Override
  public OAuth2ConnectionFactoryConfiguration get(final SocialProvider provider) {
    return providers.get(provider.getId());
  }

}
