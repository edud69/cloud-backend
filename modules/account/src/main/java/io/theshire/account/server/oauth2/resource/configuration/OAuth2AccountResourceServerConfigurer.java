

package io.theshire.account.server.oauth2.resource.configuration;

import io.theshire.common.server.configuration.oauth2.OAuth2ResourceServerConfigurer;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;


public class OAuth2AccountResourceServerConfigurer extends OAuth2ResourceServerConfigurer {


  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    super.configure(resources);
    resources.resourceId(OAuth2ResourceIdentifier.AccountService.getResourceId());
  }
}
