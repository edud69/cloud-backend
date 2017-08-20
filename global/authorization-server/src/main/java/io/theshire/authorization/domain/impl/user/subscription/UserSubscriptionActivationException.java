

package io.theshire.authorization.domain.impl.user.subscription;

import io.theshire.authorization.domain.AuthorizationErrorCodeConstants;
import io.theshire.common.domain.exception.DomainException;


public class UserSubscriptionActivationException extends DomainException {

 
  private static final long serialVersionUID = 5853702760478533596L;

 
  public UserSubscriptionActivationException(final String errorCode, final String message) {
    super(errorCode, message);
  }

 
  public static class UserSubscriptionActivationExceptionBuilder {

   
    private UserSubscriptionActivationException exception;

   
    public UserSubscriptionActivationExceptionBuilder
        asInvalidConfirmationToken(final String message) {
      exception = new UserSubscriptionActivationException(
          AuthorizationErrorCodeConstants.USERSUB_INVALID_CONFIRMATION_TOKEN, message);
      return this;
    }

   
    public UserSubscriptionActivationExceptionBuilder
        asNoConfirmationTokenFound(final String message) {
      exception = new UserSubscriptionActivationException(
          AuthorizationErrorCodeConstants.USERSUB_NO_CONFIRMATION_TOKEN, message);
      return this;
    }

   
    public UserSubscriptionActivationExceptionBuilder
        asStateNotReadyForActivation(final String message) {
      exception = new UserSubscriptionActivationException(
          AuthorizationErrorCodeConstants.USERSUB_NOT_READY_FOR_ACTIVATION, message);
      return this;
    }

   
    public UserSubscriptionActivationException build() {
      return exception;
    }

  }

}
