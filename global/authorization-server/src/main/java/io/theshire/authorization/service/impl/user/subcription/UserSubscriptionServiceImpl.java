

package io.theshire.authorization.service.impl.user.subcription;

import io.theshire.authorization.domain.impl.user.authentication.UserAuthenticationInvalidPasswordFormatException;
import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionActivationException;
import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionAlreadyExistsException;
import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionNotFoundException;
import io.theshire.authorization.service.user.subscription.UserSubscriptionActivationInPort;
import io.theshire.authorization.service.user.subscription.UserSubscriptionRequestInPort;
import io.theshire.authorization.service.user.subscription.UserSubscriptionService;
import io.theshire.common.service.OutPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@Transactional
class UserSubscriptionServiceImpl implements UserSubscriptionService {

 
  @Autowired
  private UserSubscriptionInvitationService userSubscriptionInvitationService;

 
  @Autowired
  private UserSubcriptionActivationService userSubcriptionActivationService;


  @Override
  public void processInvitationRequest(final UserSubscriptionRequestInPort input,
      OutPort<Boolean, ?> output) throws UserSubscriptionAlreadyExistsException,
      UserAuthenticationInvalidPasswordFormatException {
    userSubscriptionInvitationService.process(input.getEmail(), input.getPassword(),
        input.getTenantId());
    output.receive(true);
  }


  @Override
  public void processActivationRequest(final UserSubscriptionActivationInPort input,
      final OutPort<Boolean, ?> output)
      throws UserSubscriptionNotFoundException, UserSubscriptionActivationException {
    userSubcriptionActivationService.process(input.getEmail(), input.getToken(),
        input.getTenantId());
    output.receive(true);
  }

}
