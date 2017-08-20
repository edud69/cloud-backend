

package io.theshire.authorization.endpoint.user.subscription.adapters;

import io.theshire.authorization.endpoint.user.subscription.message.UserSubscriptionRequestMessage;
import io.theshire.authorization.service.user.subscription.UserSubscriptionRequestInPort;


public class UserSubscriptionRequestAdapter implements UserSubscriptionRequestInPort {

 
  private final UserSubscriptionRequestMessage message;

 
  public UserSubscriptionRequestAdapter(final UserSubscriptionRequestMessage subscriptionRequest) {
    this.message = subscriptionRequest;
  }


  @Override
  public String getEmail() {
    return message.getEmail();
  }


  @Override
  public String getPassword() {
    return message.getPassword();
  }


  @Override
  public String getTenantId() {
    return message.getTenantId();
  }

}
