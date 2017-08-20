

package io.theshire.chat.endpoint.websocket.adapters;

import io.theshire.chat.endpoint.websocket.message.ChatPrivateMsg;
import io.theshire.chat.service.ChatSendPrivateInPort;


public class ChatPrivateMessageSendAdapter implements ChatSendPrivateInPort {

 
  private final ChatPrivateMsg message;

 
  public ChatPrivateMessageSendAdapter(final ChatPrivateMsg message) {
    this.message = message;
  }

 
  @Override
  public String getChat() {
    return message.getMessage();
  }

 
  @Override
  public String getTargetUsername() {
    return message.getTargetUsername();
  }

}
