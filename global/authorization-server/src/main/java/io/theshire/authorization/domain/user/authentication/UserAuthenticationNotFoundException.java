

package io.theshire.authorization.domain.user.authentication;

import io.theshire.authorization.domain.AuthorizationErrorCodeConstants;
import io.theshire.common.domain.exception.DomainEntityNotFoundException;


public class UserAuthenticationNotFoundException extends DomainEntityNotFoundException {

 
  private static final long serialVersionUID = 5529899722954321844L;

 
  public UserAuthenticationNotFoundException(String message) {
    super(AuthorizationErrorCodeConstants.USERAUTH_NOT_FOUND, message, UserAuthentication.class);
  }

}
