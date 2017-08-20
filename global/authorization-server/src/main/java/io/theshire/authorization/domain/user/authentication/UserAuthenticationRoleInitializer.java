

package io.theshire.authorization.domain.user.authentication;

import io.theshire.authorization.domain.role.Role;
import io.theshire.authorization.domain.role.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Component
@Transactional
public class UserAuthenticationRoleInitializer {

 
  @Autowired
  private RoleRepository roleRepository;

 
  public UserAuthentication fetchTenantCtxRoles(final UserAuthentication ua,
      final Set<Role> roles) {
    Set<Role> userRoles = roles;
    if (ua != null) {
      if (userRoles == null) {
        userRoles = roleRepository.findByUserId(ua.getId());
      }
      ua.setRoles(userRoles);
    }
    return ua;
  }

}
