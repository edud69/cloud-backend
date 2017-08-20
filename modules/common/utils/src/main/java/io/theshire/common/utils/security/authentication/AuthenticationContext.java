

package io.theshire.common.utils.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class AuthenticationContext {

 
  private static final ThreadLocal<AuthenticationInfo> authInfo = new ThreadLocal<>();

 
  public static final AuthenticationInfo get() {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    AuthenticationInfo cached = authInfo.get();

    if (!matches(auth, cached)) {
      cached = null;
    }

    if (cached == null) {
      cached = getFromAuth(SecurityContextHolder.getContext().getAuthentication());
      authInfo.set(cached);
    }

    return cached;
  }

 
  public static final void clear() {
    authInfo.remove();
  }

 
  public static final AuthenticationInfo getFromAuth(final Authentication authentication) {
    final AuthenticationInfo cached = authInfo.get();
    if (matches(authentication, cached)) {
      return cached;
    }
    return new AuthenticationInfo(authentication);
  }

 
  private static final boolean matches(final Authentication auth,
      final AuthenticationInfo authInfo) {
    if (auth == null && authInfo == null) {
      return true;
    } else if (auth != null && authInfo != null
        && auth.hashCode() == authInfo.getAuthenticationHashCode()) {
      return true;
    }
    return false;
  }

}
