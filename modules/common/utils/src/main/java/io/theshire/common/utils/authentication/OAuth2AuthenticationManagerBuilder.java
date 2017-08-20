

package io.theshire.common.utils.authentication;

import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;

import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.Assert;


public class OAuth2AuthenticationManagerBuilder {

 
  private final OAuth2AuthenticationManager oauth2AuthenticationManager;

 
  private OAuth2ResourceIdentifier resource;

 
  private JwtAccessTokenConverter jwtTokenEnhancer;

 
  public OAuth2AuthenticationManagerBuilder() {
    this.oauth2AuthenticationManager = new OAuth2AuthenticationManager();
  }

 
  public OAuth2AuthenticationManagerBuilder resource(final OAuth2ResourceIdentifier resource) {
    this.resource = resource;
    return this;
  }

 
  public OAuth2AuthenticationManagerBuilder
      jwtTokenEnhancer(final JwtAccessTokenConverter jwtTokenEnhancer) {
    this.jwtTokenEnhancer = jwtTokenEnhancer;
    return this;
  }

 
  public OAuth2AuthenticationManager build() {
    Assert.notNull(resource, "resource cannot be null.");
    Assert.notNull(jwtTokenEnhancer, "jwtTokenEnhancer cannot be null.");

    oauth2AuthenticationManager.setResourceId(this.resource.getResourceId());
    final DefaultTokenServices tokenServices = new DefaultTokenServices();
    tokenServices.setTokenStore(new JwtTokenStore(jwtTokenEnhancer));
    tokenServices.setSupportRefreshToken(true);
    oauth2AuthenticationManager.setTokenServices(tokenServices);

    return this.oauth2AuthenticationManager;
  }

}
