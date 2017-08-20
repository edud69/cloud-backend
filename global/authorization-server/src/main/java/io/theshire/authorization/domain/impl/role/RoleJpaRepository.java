

package io.theshire.authorization.domain.impl.role;

import io.theshire.authorization.domain.role.Role;
import io.theshire.authorization.domain.role.RoleLabel;
import io.theshire.common.domain.impl.repository.JpaSpringRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RoleJpaRepository extends JpaSpringRepository<Role> {

 
  @Query("SELECT r FROM Role r WHERE r.label = :label")
  Role findByRolename(@Param("label") final RoleLabel roleLabel);

}
