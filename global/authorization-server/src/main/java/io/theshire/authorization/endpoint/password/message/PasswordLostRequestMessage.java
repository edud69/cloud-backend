

package io.theshire.authorization.endpoint.password.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordLostRequestMessage extends TransportMessage {


 
  private static final long serialVersionUID = -1973925046537448854L;

 
  private String username;

}
