

package io.theshire.authorization.domain.impl.password;

import io.theshire.authorization.domain.password.PasswordLostRequest;
import io.theshire.authorization.domain.password.PasswordLostRequestRepository;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Component
@Transactional
class PasswordLostRequestRepositoryImpl implements PasswordLostRequestRepository {

 
  @Autowired
  private PasswordLostRequestJpaSingleTenantRepository passwordLostRequestJpaRepository;

 
  @Autowired
  private UserAuthenticationRepository userAuthRepository;


  @Override
  public PasswordLostRequest findById(Long id) {
    return passwordLostRequestJpaRepository.findOne(id);
  }


  @Override
  public PasswordLostRequest save(PasswordLostRequest domainObject) {
    return passwordLostRequestJpaRepository.save(domainObject);
  }


  @Override
  public Set<PasswordLostRequest> findByRequestedUsername(String username) {
    return passwordLostRequestJpaRepository
        .findByUserId(userAuthRepository.findByUsername(username).getId());
  }


  @Override
  public void deleteLostPasswordRequestsForUser(String username) {
    passwordLostRequestJpaRepository
        .deleteAllByUserId(userAuthRepository.findByUsername(username).getId());
  }

}
