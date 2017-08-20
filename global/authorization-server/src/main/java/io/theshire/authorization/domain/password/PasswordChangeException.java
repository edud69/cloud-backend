

package io.theshire.authorization.domain.password;

import io.theshire.authorization.domain.AuthorizationErrorCodeConstants;
import io.theshire.common.domain.exception.DomainException;


public class PasswordChangeException extends DomainException {

 
  private static final long serialVersionUID = -8822936124401095354L;

 
  private PasswordChangeException(final String errorCode, final String message) {
    super(errorCode, message);
  }

 
  public static class PasswordChangeExceptionBuilder {

   
    private PasswordChangeException exception;

   
    public PasswordChangeExceptionBuilder
        asChangingAnotherUserPasswordIsForbidden(final String message) {
      exception = new PasswordChangeException(
          AuthorizationErrorCodeConstants.PASSWORD_CHANGE_OTHER_USER_PASS_FORBIDDEN, message);
      return this;
    }

   
    public PasswordChangeExceptionBuilder asPreviousPasswordCheckMismatch(final String message) {
      exception = new PasswordChangeException(
          AuthorizationErrorCodeConstants.PASSWORD_CHANGE_PREVIOUS_PASS_MISMATCH, message);
      return this;
    }

   
    public PasswordChangeExceptionBuilder asNoLostPasswordRequestFound(final String message) {
      exception = new PasswordChangeException(
          AuthorizationErrorCodeConstants.PASSWORD_NO_LOST_PASS_REQUEST_FOUND, message);
      return this;
    }

   
    public PasswordChangeException build() {
      return exception;
    }

  }

}
