

package io.theshire.authorization.domain.impl.role;

import io.theshire.authorization.domain.role.Role;
import io.theshire.authorization.domain.role.RoleLabel;
import io.theshire.authorization.domain.role.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class RoleRepositoryImpl implements RoleRepository {

 
  @Autowired
  private UserRoleJpaRepository userRoleJpaRepository;

 
  @Autowired
  private RoleJpaRepository roleJpaRepository;


  @Override
  public Set<Role> findByUserId(final Long userId) {
    final Set<UserRole> urs = userRoleJpaRepository.findByUserId(userId);
    return urs == null ? new HashSet<>()
        : urs.stream().map(ur -> ur.getRole()).collect(Collectors.toSet());
  }


  @Override
  public Role findById(final Long id) {
    return roleJpaRepository.findOne(id);
  }


  @Override
  public Role save(final Role domainObject) {
    return roleJpaRepository.save(domainObject);
  }


  @Override
  public Set<Role> saveUserRoles(final Long userId, final Set<Role> roles) {
    Set<UserRole> userRoles = userRoleJpaRepository.findByUserId(userId);
    if (userRoles == null) {
      userRoles = new HashSet<>();
    }

    // delete user roles that do not belong to the user anymore
    final Set<UserRole> userRolesToRemove = new HashSet<>();
    final Set<String> rolenames = roles.stream().map(r -> r.getName()).collect(Collectors.toSet());

    userRolesToRemove.addAll(userRoles.stream()
        .filter(ur -> !rolenames.contains(ur.getRole().getName())).collect(Collectors.toSet()));

    if (!userRolesToRemove.isEmpty()) {
      userRoleJpaRepository.deleteInBatch(userRolesToRemove);
      userRoles.removeAll(userRolesToRemove);
    }

    // adds the new user roles
    final Set<UserRole> userRolesToAdd = new HashSet<>();
    final Set<String> currentUserRolenames =
        userRoles.stream().map(ur -> ur.getRole().getName()).collect(Collectors.toSet());

    roles.stream().filter(r -> !currentUserRolenames.contains(r.getName()))
        .forEach(r -> userRolesToAdd.add(new UserRole(userId, r)));

    userRoles.addAll(userRolesToAdd);

    return userRoleJpaRepository.save(userRoles).stream().map(ur -> ur.getRole())
        .collect(Collectors.toSet());
  }


  @Override
  public Role findByRolename(RoleLabel role) {
    return roleJpaRepository.findByRolename(role);
  }

}
