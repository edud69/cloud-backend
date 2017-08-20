

package io.theshire.chat.endpoint.websocket.message;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data


@EqualsAndHashCode(callSuper = true)
public class ChatPrivateMsg extends ChatMsg {

 
  private static final long serialVersionUID = -4010027339595876196L;

 
  private String targetUsername;
}
