

package io.theshire.chat.endpoint.websocket.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;




@Data


@EqualsAndHashCode(callSuper = true)
public class ParticipantJoinEventMsg extends TransportMessage {

 
  private static final long serialVersionUID = -2131583256125544331L;

 
  private String participantName;

 
  private LocalDateTime joinTime;

 
  private String channelName;

}
