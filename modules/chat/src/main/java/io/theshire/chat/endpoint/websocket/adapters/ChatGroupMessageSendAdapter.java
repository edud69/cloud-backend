

package io.theshire.chat.endpoint.websocket.adapters;

import io.theshire.chat.endpoint.websocket.message.ChatGroupMsg;
import io.theshire.chat.service.ChatSendGroupInPort;


public class ChatGroupMessageSendAdapter implements ChatSendGroupInPort {

 
  private ChatGroupMsg message;

 
  public ChatGroupMessageSendAdapter(final ChatGroupMsg chatMsg) {
    this.message = chatMsg;
  }

 
  @Override
  public String getChat() {
    return message.getMessage();
  }

 
  @Override
  public String getChannelName() {
    return message.getChannelName();
  }

}
