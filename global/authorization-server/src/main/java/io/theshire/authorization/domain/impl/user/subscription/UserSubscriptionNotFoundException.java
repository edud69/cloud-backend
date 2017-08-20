

package io.theshire.authorization.domain.impl.user.subscription;

import io.theshire.authorization.domain.AuthorizationErrorCodeConstants;
import io.theshire.authorization.domain.user.subscription.UserSubscription;
import io.theshire.common.domain.exception.DomainEntityNotFoundException;


public class UserSubscriptionNotFoundException extends DomainEntityNotFoundException {

 
  private static final long serialVersionUID = 3922574567039821819L;

 
  public UserSubscriptionNotFoundException(String message) {
    super(AuthorizationErrorCodeConstants.USERSUB_NOT_FOUND, message, UserSubscription.class);
  }

}
