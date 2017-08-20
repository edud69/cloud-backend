

package io.theshire.common.utils.security.authentication;

import io.theshire.common.utils.security.constants.SecurityConstants;

import lombok.Getter;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class AuthenticationInfo {

 
  @Getter
  private String username;

 

 
  @Getter
  private String tenantIdentifier;

 
 
  @Getter
  private String jwtToken;

 
  @Getter
  private LocalDateTime jwtTokenCreationTime;

 
  @Getter
  private LocalDateTime jwtTokenExpireTime;

 
 
  @Getter
  private Set<String> permissionNames;

 
 
  @Getter
  private Set<String> roleNames;

 
 
  @Getter
  private boolean anonymousAuthentication;

 
 
  @Getter
  private int authenticationHashCode = -1;

 
  @Getter
  private Long userId;

 
  protected AuthenticationInfo(final Authentication auth) {
    if (auth != null) {
      authenticationHashCode = auth.hashCode();
      if (auth instanceof AnonymousAuthenticationToken) {
        anonymousAuthentication = true;
      } else if (!(auth instanceof UsernamePasswordAuthenticationToken)
          && !(auth instanceof OAuth2Authentication)) {
        throw new UnsupportedOperationException(
            "Invalid operation, authentication must be of OAuth2Authentication type.");
      }

      username = auth.getName();
      extractDetails(auth);
      extractRoles(auth);
      extractPermissions(auth);
    } else {
      anonymousAuthentication = true;
    }
  }

 
  private void extractRoles(final Authentication auth) {
    if (auth.getAuthorities() != null) {
      roleNames = Collections.unmodifiableSet(auth.getAuthorities().stream()
          .filter(a -> a.getAuthority().startsWith(SecurityConstants.ROLE_PREFIX))
          .map(a -> a.getAuthority()).collect(Collectors.toSet()));
    }
  }

 
  private void extractPermissions(final Authentication auth) {
    if (auth.getAuthorities() != null) {
      permissionNames = Collections.unmodifiableSet(auth.getAuthorities().stream()
          .filter(a -> !a.getAuthority().startsWith(SecurityConstants.ROLE_PREFIX))
          .map(a -> a.getAuthority()).collect(Collectors.toSet()));
    }
  }

 
  @SuppressWarnings("unchecked")
  private void extractDetails(final Authentication auth) {
    if (auth.getDetails() != null && auth.getDetails() instanceof OAuth2AuthenticationDetails) {
      jwtToken = ((OAuth2AuthenticationDetails) auth.getDetails()).getTokenValue();
    }
    if (auth instanceof OAuth2Authentication
        && ((OAuth2Authentication) auth).getUserAuthentication().getDetails() != null) {
      final Map<String, Object> details =
          (Map<String, Object>) ((OAuth2Authentication) auth).getUserAuthentication().getDetails();
      doExtractDetails(details);
    } else if (auth.getDetails() != null && auth.getDetails() instanceof Map) {
      doExtractDetails((Map<String, Object>) auth.getDetails());
    }
  }

 
  private void doExtractDetails(final Map<String, Object> details) {
    if (details != null) {
      final AuthenticationDetails authenticationDetailsExtractor =
          new AuthenticationDetails(details);
      tenantIdentifier = authenticationDetailsExtractor.getTenantIdentifier();
      userId = authenticationDetailsExtractor.getUserId();
      if (jwtToken == null) {
        jwtToken = authenticationDetailsExtractor.getJwtToken();
        jwtTokenCreationTime = authenticationDetailsExtractor.getJwtTokenCreationTime();
        jwtTokenExpireTime = authenticationDetailsExtractor.getJwtTokenExpireTime();
      }
    }
  }

}
