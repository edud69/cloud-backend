

package io.theshire.authorization.domain.impl.user.authentication;

import io.theshire.authorization.domain.role.Role;
import io.theshire.authorization.domain.role.RoleRepository;
import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationRepository;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationRoleInitializer;
import io.theshire.common.utils.transaction.TransactionUtils;

import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Component
@Transactional
public class UserAuthenticationRepositoryImpl implements UserAuthenticationRepository {

 
  @Autowired
  private UserAuthenticationJpaSingleTenantRepository userAuthenticationJpaRepository;

 
  @Autowired
  private RoleRepository roleRepository;

 
  @Autowired
  private UserAuthenticationRoleInitializer userAuthenticationRoleInitializer;

 
  @Autowired
  private TransactionUtils transactionUtils;


  @Override
  public UserAuthentication findById(final Long id) {
    return userAuthenticationRoleInitializer
        .fetchTenantCtxRoles(userAuthenticationJpaRepository.findOne(id), null);
  }


  @Override
  public UserAuthentication save(final UserAuthentication domainObject) {
    final MutableObject<UserAuthentication> previousState = new MutableObject<>();
    final MutableObject<Long> savedUserAuthId = new MutableObject<>();

    transactionUtils.runAfterRollback(() -> {
      // if transaction fails at any time, a restore must be made on the single-tenant transaction
      // manager
      // we must restore the state manually
      if (previousState.getValue() != null) {
        userAuthenticationJpaRepository.save(previousState.getValue());
      } else if (savedUserAuthId.getValue() != null) {
        userAuthenticationJpaRepository.delete(savedUserAuthId.getValue());
      }
    });

    previousState.setValue(domainObject.getId() == null ? null
        : userAuthenticationJpaRepository.findOne(domainObject.getId()));

    final UserAuthentication saved = userAuthenticationJpaRepository.save(domainObject);
    final Set<Role> roles = roleRepository.saveUserRoles(saved.getId(), domainObject.getRoles());
    final UserAuthentication userAuth =
        userAuthenticationRoleInitializer.fetchTenantCtxRoles(saved, roles);

    savedUserAuthId.setValue(saved.getId());

    return userAuth;
  }


  @Override
  public UserAuthentication findByUsername(final String username) {
    final UserAuthentication userAuth = userAuthenticationJpaRepository.findByUsername(username);
    return userAuthenticationRoleInitializer.fetchTenantCtxRoles(userAuth, null);
  }

}
