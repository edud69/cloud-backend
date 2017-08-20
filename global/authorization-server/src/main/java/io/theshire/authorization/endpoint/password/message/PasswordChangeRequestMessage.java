

package io.theshire.authorization.endpoint.password.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;




@Data


@EqualsAndHashCode(callSuper = true)
public class PasswordChangeRequestMessage extends TransportMessage {

 
  private static final long serialVersionUID = -2398014754535831516L;

 
  private String username;

 
  private boolean useLostPasswordToken;

 
  private String lostPasswordToken;

 
  private String oldPassword;

 
  private String newPassword;

}
