

package io.theshire.authorization.domain.impl.user.authentication;

import io.theshire.authorization.domain.AuthorizationErrorCodeConstants;
import io.theshire.common.domain.exception.DomainException;


public class UserAuthenticationInvalidPasswordFormatException extends DomainException {

 
  private static final long serialVersionUID = -5391124063080689550L;

 
  public UserAuthenticationInvalidPasswordFormatException(String message) {
    super(AuthorizationErrorCodeConstants.PASSWORD_INVALID_FORMAT, message);
  }

}
