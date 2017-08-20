

package io.theshire.common.server.configuration.permission;

import io.theshire.common.utils.security.authentication.AuthenticationContext;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Set;


@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {


  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject,
      Object permission) {
    final Set<String> permissions = AuthenticationContext.getFromAuth(authentication)
        .getPermissionNames();
    if (!permissions.contains(permission)) {
      log.warn("Denying user {} permission '{}' on object {}.", authentication.getName(),
          permission, targetDomainObject);
      return false;
    }
    return true;
  }


  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId,
      String targetType, Object permission) {
    final Set<String> permissions = AuthenticationContext.getFromAuth(authentication)
        .getPermissionNames();
    if (!permissions.contains(permission)) {
      log.warn("Denying user {} permission '{}' on targetClass {} and targetId {}.",
          authentication.getName(), permission, targetType, targetId);
      return false;
    }
    return true;
  }

}
