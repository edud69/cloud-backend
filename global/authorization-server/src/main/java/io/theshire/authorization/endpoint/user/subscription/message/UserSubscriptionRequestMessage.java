

package io.theshire.authorization.endpoint.user.subscription.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;


@Data


@EqualsAndHashCode(callSuper = true)
public class UserSubscriptionRequestMessage extends TransportMessage {

 
  private static final long serialVersionUID = 7464007560337808820L;

 
  private String email;

 
  private String tenantId;

 
  private String password;

 
  private LocalDateTime requestTime;
}
