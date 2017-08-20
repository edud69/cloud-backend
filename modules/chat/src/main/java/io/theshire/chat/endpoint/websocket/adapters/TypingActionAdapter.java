

package io.theshire.chat.endpoint.websocket.adapters;

import io.theshire.chat.endpoint.websocket.message.TypingActionMsg;
import io.theshire.chat.service.action.TypingActionInPort;


public class TypingActionAdapter implements TypingActionInPort {

 
  private final TypingActionMsg message;

 
  public TypingActionAdapter(final TypingActionMsg message) {
    this.message = message;
  }

 
  @Override
  public String getTargetUsername() {
    return message.getTargetUsername();
  }

 
  @Override
  public String getChannelName() {
    return message.getChannelName();
  }

}
