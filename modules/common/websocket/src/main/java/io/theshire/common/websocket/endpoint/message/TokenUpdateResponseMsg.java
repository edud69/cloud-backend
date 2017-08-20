

package io.theshire.common.websocket.endpoint.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = true)
public class TokenUpdateResponseMsg extends TransportMessage {

 
  private static final long serialVersionUID = -674907883408599208L;

 
  private LocalDateTime tokenUpdateTime;
}
