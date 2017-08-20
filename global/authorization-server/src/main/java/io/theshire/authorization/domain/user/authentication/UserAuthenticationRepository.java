

package io.theshire.authorization.domain.user.authentication;

import io.theshire.common.domain.DomainRepository;


public interface UserAuthenticationRepository extends DomainRepository<UserAuthentication> {

 
  UserAuthentication findByUsername(final String username);

}
