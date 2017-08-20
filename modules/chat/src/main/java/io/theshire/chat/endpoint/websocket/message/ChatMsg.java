

package io.theshire.chat.endpoint.websocket.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;


@Data


@EqualsAndHashCode(callSuper = true)
public abstract class ChatMsg extends TransportMessage {

 
  private static final long serialVersionUID = 8274031548017605850L;

 
  private String message;

 
  private String senderUsername;

 
  private LocalDateTime sentTime;

}
