

package io.theshire.authorization.domain.impl.password;

import io.theshire.authorization.domain.password.PasswordLostRequest;
import io.theshire.common.domain.impl.repository.JpaSpringRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;


public interface PasswordLostRequestJpaSingleTenantRepository
    extends
      JpaSpringRepository<PasswordLostRequest> {

 
  @Modifying
  @Query("delete from PasswordLostRequest where requestedFor.id = :userId")
  void deleteAllByUserId(@Param("userId") final long userId);

 
  @Query("from PasswordLostRequest where requestedFor.id = :userId")
  Set<PasswordLostRequest> findByUserId(@Param("userId") final long userId);

}
