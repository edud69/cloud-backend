

package io.theshire.common.utils.security.authentication;

import io.theshire.common.utils.security.constants.SecurityAuthenticationKeyDetailConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RunWith(MockitoJUnitRunner.class)
public class AuthenticationInfoTest {

 
  @Mock
  private OAuth2Authentication oauth2Auth;

 
  @Mock
  private UsernamePasswordAuthenticationToken usernamePasswordAuth;

 
  @Mock
  private AnonymousAuthenticationToken anonymousAuth;

 
  private Set<String> permissions = new HashSet<>();

 
  private Set<String> roles = new HashSet<>();

 
  private Set<String> authorities = new HashSet<>();

 
  @Before
  public void setup() {
    // create authorities
    permissions.addAll(Arrays.asList(new String[] { "PERM_1", "PERM_2", "ANOTHER_PERM" }));
    roles.addAll(Arrays.asList(new String[] { "ROLE_1", "ROLE_2" }));
    authorities.addAll(permissions);
    authorities.addAll(roles);
  }

 
  @Test
  public void shouldBeAnonymousIfNoAuthentication() {
    final AuthenticationInfo authInfo = new AuthenticationInfo(null);
    Assert.assertTrue(authInfo.isAnonymousAuthentication());
  }

 
  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowIfNotOAuth2Authentication() {
    final Authentication auth = Mockito.mock(Authentication.class);
    Assert.fail("Should have throw : " + new AuthenticationInfo(auth));
  }

 
  @Test
  public void shouldBeAnonymousIfAuthIsAnonymous() {
    Assert.assertTrue(new AuthenticationInfo(anonymousAuth).isAnonymousAuthentication());
  }

 
  @Test
  public void shouldBuildWithUsernameAuthenticationToken() {
    final Map<String, Object> details = new HashMap<String, Object>();
    details.put(SecurityAuthenticationKeyDetailConstants.TENANT_IDENTIFIER, "aTenant");
    details.put(SecurityAuthenticationKeyDetailConstants.USER_ID, 1234L);
    details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN, "aJwtToken");
    details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_CREATION_TIME, 0L);
    details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_EXPIRE_TIME, 1L);

    Mockito.when(usernamePasswordAuth.getName()).thenReturn("aName");
    Mockito.when(usernamePasswordAuth.getAuthorities())
        .thenReturn(toGrantedAuthorities(authorities));
    Mockito.when(usernamePasswordAuth.getDetails()).thenReturn(details);

    final AuthenticationInfo authInfo = new AuthenticationInfo(usernamePasswordAuth);

    Assert.assertEquals("aName", authInfo.getUsername());
    Assert.assertEquals(roles, authInfo.getRoleNames());
    Assert.assertEquals(permissions, authInfo.getPermissionNames());
    Assert.assertEquals("aTenant", authInfo.getTenantIdentifier());
    Assert.assertEquals("aJwtToken", authInfo.getJwtToken());
    Assert.assertEquals((Long) 1234L, authInfo.getUserId());
    Assert.assertEquals(
        LocalDateTime.ofInstant(Instant.ofEpochSecond(0L), ZoneId.of(ZoneOffset.UTC.getId())),
        authInfo.getJwtTokenCreationTime());
    Assert.assertEquals(
        LocalDateTime.ofInstant(Instant.ofEpochSecond(1L), ZoneId.of(ZoneOffset.UTC.getId())),
        authInfo.getJwtTokenExpireTime());
  }

 
  @Test
  public void shouldBuildWithOAuth2AuthenticationToken() {
    final Map<String, Object> details = new HashMap<String, Object>();
    details.put(SecurityAuthenticationKeyDetailConstants.TENANT_IDENTIFIER, "aTenant");
    details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN, "aJwtToken");
    details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_CREATION_TIME, 0L);
    details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_EXPIRE_TIME, 1L);

    Mockito.when(oauth2Auth.getName()).thenReturn("aName");
    Mockito.when(oauth2Auth.getAuthorities()).thenReturn(toGrantedAuthorities(authorities));
    Mockito.when(usernamePasswordAuth.getDetails()).thenReturn(details);
    Mockito.when(oauth2Auth.getUserAuthentication()).thenReturn(usernamePasswordAuth);

    final AuthenticationInfo authInfo = new AuthenticationInfo(oauth2Auth);

    Assert.assertEquals("aName", authInfo.getUsername());
    Assert.assertEquals(roles, authInfo.getRoleNames());
    Assert.assertEquals(permissions, authInfo.getPermissionNames());
    Assert.assertEquals("aTenant", authInfo.getTenantIdentifier());
    Assert.assertEquals("aJwtToken", authInfo.getJwtToken());
    Assert.assertEquals(
        LocalDateTime.ofInstant(Instant.ofEpochSecond(0L), ZoneId.of(ZoneOffset.UTC.getId())),
        authInfo.getJwtTokenCreationTime());
    Assert.assertEquals(
        LocalDateTime.ofInstant(Instant.ofEpochSecond(1L), ZoneId.of(ZoneOffset.UTC.getId())),
        authInfo.getJwtTokenExpireTime());
  }

 
  private Set<GrantedAuthority> toGrantedAuthorities(final Set<String> authorities) {
    final Set<GrantedAuthority> gas = new HashSet<>();
    gas.addAll(authorities.stream().map(auth -> new SimpleGrantedAuthority(auth))
        .collect(Collectors.toSet()));
    return gas;
  }

}
