

package io.theshire.account.domain.impl;

import io.theshire.account.domain.Account;
import io.theshire.common.domain.impl.repository.JpaSpringRepository;
import org.springframework.data.repository.query.Param;


public interface AccountJpaRepository extends JpaSpringRepository<Account> {

  Account findByUserId(@Param("userId") final Long userId);

  void deleteByUserId(@Param("userId") final Long userId);

}
