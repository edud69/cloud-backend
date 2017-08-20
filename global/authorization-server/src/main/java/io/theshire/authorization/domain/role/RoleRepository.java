

package io.theshire.authorization.domain.role;

import io.theshire.common.domain.DomainRepository;

import java.util.Set;


public interface RoleRepository extends DomainRepository<Role> {

 
  Set<Role> findByUserId(final Long userId);

 
  Set<Role> saveUserRoles(final Long userId, final Set<Role> roles);

 
  Role findByRolename(final RoleLabel role);

}
