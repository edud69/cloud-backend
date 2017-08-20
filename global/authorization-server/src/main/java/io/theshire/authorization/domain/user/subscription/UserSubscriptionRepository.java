

package io.theshire.authorization.domain.user.subscription;

import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.common.domain.DomainRepository;


public interface UserSubscriptionRepository extends DomainRepository<UserSubscription> {

 
  UserSubscription findByUserAuthentication(final UserAuthentication userAuthentication);

}
