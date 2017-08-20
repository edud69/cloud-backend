

package io.theshire.chat.endpoint.websocket.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;


@Data


@EqualsAndHashCode(callSuper = true)
public class TypingActionMsg extends TransportMessage {

 
  private static final long serialVersionUID = -4640319602445802853L;

 
  private String author;

 
  private String channelName;

 
  private LocalDateTime actionTime;

 
  private String targetUsername;

}
