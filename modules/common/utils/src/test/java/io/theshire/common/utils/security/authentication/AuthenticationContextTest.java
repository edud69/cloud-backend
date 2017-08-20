

package io.theshire.common.utils.security.authentication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextHolder.class)
public class AuthenticationContextTest {

 
  @Mock
  private SecurityContext securityContext;

 
  @Mock
  private AuthenticationInfo authInfo;

 
  private Authentication auth;

 
  private final int authHashCode = 123456789;

 
  private class AnonymousAuthenticationExt extends AnonymousAuthenticationToken {

   
    private static final long serialVersionUID = -4380661489578553384L;

   
    public AnonymousAuthenticationExt(String key, Object principal,
        Collection<? extends GrantedAuthority> authorities) {
      super(key, principal, authorities);
    }

  
    @Override
    public int hashCode() {
      return authHashCode;
    }

  }

 
  @Before
  public void setup() {
    PowerMockito.mockStatic(SecurityContextHolder.class);
    PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(securityContext);

    this.auth = new AnonymousAuthenticationExt("forTest", "forTest2",
        Sets.newSet(new SimpleGrantedAuthority("aRole")));
  }

 
  @Test
  public void shouldReturnTheCachedAuthInfo() {
    @SuppressWarnings("unchecked")
    final ThreadLocal<AuthenticationInfo> threadLocal =
        (ThreadLocal<AuthenticationInfo>) ReflectionTestUtils.getField(AuthenticationContext.class,
            "authInfo");
    threadLocal.set(authInfo);

    Mockito.when(authInfo.getAuthenticationHashCode()).thenReturn(authHashCode);
    Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
    final AuthenticationInfo rez = AuthenticationContext.get();

    Assert.assertEquals(authInfo, rez);
  }

 
  @Test
  public void shouldCreateANewContextWhenNoMatch() {
    @SuppressWarnings("unchecked")
    final ThreadLocal<AuthenticationInfo> threadLocal =
        (ThreadLocal<AuthenticationInfo>) ReflectionTestUtils.getField(AuthenticationContext.class,
            "authInfo");
    threadLocal.set(authInfo);

    Mockito.when(authInfo.getAuthenticationHashCode()).thenReturn(authHashCode + 1);
    Mockito.when(securityContext.getAuthentication()).thenReturn(this.auth);
    final AuthenticationInfo rez = AuthenticationContext.get();

    Assert.assertNotNull(rez);
    Assert.assertNotEquals(authInfo, rez);
  }

 
  @Test
  public void shouldCreateANewContextWhenNoAuth() {
    @SuppressWarnings("unchecked")
    final ThreadLocal<AuthenticationInfo> threadLocal =
        (ThreadLocal<AuthenticationInfo>) ReflectionTestUtils.getField(AuthenticationContext.class,
            "authInfo");
    threadLocal.set(null);

    Mockito.when(authInfo.getAuthenticationHashCode()).thenReturn(authHashCode + 1);
    Mockito.when(securityContext.getAuthentication()).thenReturn(this.auth);
    final AuthenticationInfo rez = AuthenticationContext.get();

    Assert.assertNotNull(rez);
    Assert.assertNotEquals(authInfo, rez);
  }

 
  @Test
  public void shouldReturnTheCachedAuthIfMatch() {
    @SuppressWarnings("unchecked")
    final ThreadLocal<AuthenticationInfo> threadLocal =
        (ThreadLocal<AuthenticationInfo>) ReflectionTestUtils.getField(AuthenticationContext.class,
            "authInfo");
    threadLocal.set(authInfo);

    Mockito.when(authInfo.getAuthenticationHashCode()).thenReturn(authHashCode);
    Mockito.when(securityContext.getAuthentication()).thenReturn(this.auth);
    final AuthenticationInfo rez = AuthenticationContext.getFromAuth(this.auth);

    Assert.assertEquals(authInfo, rez);
  }

 
  @Test
  public void shouldCreateANewAuthIfNotMatch() {
    @SuppressWarnings("unchecked")
    final ThreadLocal<AuthenticationInfo> threadLocal =
        (ThreadLocal<AuthenticationInfo>) ReflectionTestUtils.getField(AuthenticationContext.class,
            "authInfo");
    threadLocal.set(authInfo);

    Mockito.when(authInfo.getAuthenticationHashCode()).thenReturn(authHashCode + 1);
    Mockito.when(securityContext.getAuthentication()).thenReturn(this.auth);
    final AuthenticationInfo rez = AuthenticationContext.getFromAuth(this.auth);

    Assert.assertNotNull(rez);
    Assert.assertNotEquals(authInfo, rez);
  }

 
  @Test
  public void shouldClearTheAuthInfo() {
    @SuppressWarnings("unchecked")
    final ThreadLocal<AuthenticationInfo> threadLocal =
        (ThreadLocal<AuthenticationInfo>) ReflectionTestUtils.getField(AuthenticationContext.class,
            "authInfo");
    threadLocal.set(authInfo);
    AuthenticationContext.clear();
    Assert.assertNull(threadLocal.get());
  }

}
