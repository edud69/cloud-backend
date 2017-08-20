

package io.theshire.authorization.endpoint.user.subscription.adapters;

import io.theshire.authorization.endpoint.user.subscription.message.UserSubscriptionActivationRequestMessage;
import io.theshire.authorization.service.user.subscription.UserSubscriptionActivationInPort;


public class UserSubscriptionActivationAdapter implements UserSubscriptionActivationInPort {

 
  private final UserSubscriptionActivationRequestMessage message;

 
  public UserSubscriptionActivationAdapter(final UserSubscriptionActivationRequestMessage message) {
    this.message = message;
  }

 
  @Override
  public String getEmail() {
    return message.getEmail();
  }

 
  @Override
  public String getToken() {
    return message.getConfirmationToken();
  }

 
  @Override
  public String getTenantId() {
    return message.getTenantId();
  }

}
