

package io.theshire.authorization.domain.password;

import io.theshire.common.domain.DomainRepository;

import java.util.Set;


public interface PasswordLostRequestRepository extends DomainRepository<PasswordLostRequest> {

 
  Set<PasswordLostRequest> findByRequestedUsername(final String username);

 
  void deleteLostPasswordRequestsForUser(final String username);

}
