

package io.theshire.authorization.domain.impl.role;

import io.theshire.common.domain.impl.repository.JpaSpringRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;


public interface UserRoleJpaRepository extends JpaSpringRepository<UserRole> {

 
  @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId")
  Set<UserRole> findByUserId(@Param("userId") final Long userId);

}
