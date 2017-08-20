

package io.theshire.chat.endpoint.websocket.message;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data


@EqualsAndHashCode(callSuper = true)
public class ChatGroupMsg extends ChatMsg {

 
  private static final long serialVersionUID = 8280811240681743146L;

 
  private String channelName;
}
