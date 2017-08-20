

package io.theshire.common.utils.authentication;

import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;


public class OAuth2AuthenticationManagerBuilderTest {

 
  private OAuth2AuthenticationManagerBuilder classUnderTest;

 
  @Before
  public void setup() {
    this.classUnderTest = new OAuth2AuthenticationManagerBuilder();
  }

 
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIfJwtTokenEnhancerNull() {
    final OAuth2AuthenticationManager oauth2Manager = classUnderTest.jwtTokenEnhancer(null).build();
    Assert.fail("Should have thrown for : " + oauth2Manager);
  }

 
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIfResourceIdNull() {
    final OAuth2AuthenticationManager oauth2Manager = classUnderTest.resource(null).build();
    Assert.fail("Should have thrown for : " + oauth2Manager);
  }

 
  @Test
  public void shouldBuildWithNonNullArgs() {
    final OAuth2AuthenticationManager oauth2Manager =
        classUnderTest.jwtTokenEnhancer(new JwtAccessTokenConverter())
            .resource(OAuth2ResourceIdentifier.AccountService).build();
    Assert.assertNotNull(oauth2Manager);
  }

}
