

package io.theshire.authorization.domain.jwt;

import io.theshire.authorization.domain.AuthorizationErrorCodeConstants;
import io.theshire.common.domain.exception.DomainException;


public class JwtTokenRevokedException extends DomainException {

 
  private static final long serialVersionUID = 2114932302848416884L;

 
  public JwtTokenRevokedException(final String msg) {
    super(AuthorizationErrorCodeConstants.JWT_TOKEN_REVOKED, msg);
  }

}
