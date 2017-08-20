

package io.theshire.chat.endpoint.websocket.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;




@Data




@EqualsAndHashCode(callSuper = true)
public class ParticipantLeaveEventMsg extends TransportMessage {

 
  private static final long serialVersionUID = -632279958674892141L;

 
  private String participantName;

 
  private String channelName;

 
  private LocalDateTime leaveTime;

}
