

package io.theshire.authorization.domain.impl.user.subscription;

import io.theshire.authorization.domain.AuthorizationErrorCodeConstants;
import io.theshire.common.domain.exception.DomainException;


public class UserSubscriptionAlreadyExistsException extends DomainException {

 
  private static final long serialVersionUID = -2971399208874472965L;

 
  public UserSubscriptionAlreadyExistsException(String message) {
    super(AuthorizationErrorCodeConstants.USERSUB_ALREADY_EXISTS, message);
  }

}
