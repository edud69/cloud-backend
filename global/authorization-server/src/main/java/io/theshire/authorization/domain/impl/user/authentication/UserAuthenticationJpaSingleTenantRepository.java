

package io.theshire.authorization.domain.impl.user.authentication;

import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.common.domain.impl.repository.JpaSpringRepository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.query.Param;


public interface UserAuthenticationJpaSingleTenantRepository
    extends
      JpaSpringRepository<UserAuthentication> {

 
  @Query("SELECT ua FROM UserAuthentication WHERE username = :username")
  UserAuthentication findByUsername(@Param("username") final String username);

}
