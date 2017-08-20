

package io.theshire.common.websocket.endpoint.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class TokenUpdateRequestMsg extends TransportMessage {

 
  private static final long serialVersionUID = 5333168105987476555L;

 
  private String newTokenValue;

}
