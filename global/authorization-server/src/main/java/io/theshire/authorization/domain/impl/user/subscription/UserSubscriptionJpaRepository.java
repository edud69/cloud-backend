

package io.theshire.authorization.domain.impl.user.subscription;

import io.theshire.authorization.domain.user.subscription.UserSubscription;
import io.theshire.common.domain.impl.repository.JpaSpringRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserSubscriptionJpaRepository extends JpaSpringRepository<UserSubscription> {

 
  @Query("SELECT us FROM UserSubscription us WHERE us.userId = :userId")
  UserSubscription findByUserId(@Param("userId") final Long userId);

}
