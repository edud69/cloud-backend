

package io.theshire.authorization.endpoint.user.subscription.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data


@EqualsAndHashCode(callSuper = true)
public class UserSubscriptionActivationRequestMessage extends TransportMessage {

 
  private static final long serialVersionUID = 1548552745197830798L;

 
  private String tenantId;

 
  private String confirmationToken;

 
  private String email;

}
