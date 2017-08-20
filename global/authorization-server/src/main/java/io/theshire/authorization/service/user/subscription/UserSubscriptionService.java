

package io.theshire.authorization.service.user.subscription;

import io.theshire.authorization.domain.impl.user.authentication.UserAuthenticationInvalidPasswordFormatException;
import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionActivationException;
import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionAlreadyExistsException;
import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionNotFoundException;
import io.theshire.common.service.OutPort;


public interface UserSubscriptionService {

 
  void processInvitationRequest(final UserSubscriptionRequestInPort input,
      final OutPort<Boolean, ?> output) throws UserSubscriptionAlreadyExistsException,
      UserAuthenticationInvalidPasswordFormatException;

 
  void processActivationRequest(final UserSubscriptionActivationInPort input,
      final OutPort<Boolean, ?> output)
      throws UserSubscriptionNotFoundException, UserSubscriptionActivationException;

}
