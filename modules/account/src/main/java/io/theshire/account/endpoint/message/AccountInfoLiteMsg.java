

package io.theshire.account.endpoint.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data




@EqualsAndHashCode(callSuper = true)
public class AccountInfoLiteMsg extends TransportMessage {

 
  private static final long serialVersionUID = 3834856444147400024L;

 
  private Long userId;

 
  private String firstName;

 
  private String lastName;

 
  private String avatarUrl;
}
