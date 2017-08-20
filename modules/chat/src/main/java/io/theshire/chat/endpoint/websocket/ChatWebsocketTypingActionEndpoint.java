

package io.theshire.chat.endpoint.websocket;

import io.theshire.chat.domain.TypingAction;
import io.theshire.chat.endpoint.websocket.adapters.TypingActionAdapter;
import io.theshire.chat.endpoint.websocket.message.TypingActionMsg;
import io.theshire.chat.service.action.TypingActionService;
import io.theshire.common.service.OutPort;
import io.theshire.common.websocket.endpoint.message.StompMessageDestination;
import io.theshire.common.websocket.endpoint.message.StompMessagingTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


@Controller
public class ChatWebsocketTypingActionEndpoint {

 
  private static final StompMessageDestination USER_CHAT_QUEUE_DESTINATION =
      new StompMessageDestination("/queue/tenant.?-chat.action.typing");

 
  private static final StompMessageDestination CHAT_TOPIC_DESTINATION_PREFIX =
      new StompMessageDestination("/topic/tenant.?-chat.action.typing");

 
  @Autowired
  private StompMessagingTemplate stompMessagingTemplate;

 
  @Autowired
  private TypingActionService typingActionService;

 
  @MessageMapping("/chat.action.typing")
  public void typingAction(@Payload final TypingActionMsg typingActionMsg) {
    final OutPort<TypingAction, TypingActionMsg> output =
        OutPort.create(received -> toWebsocketMessage(received));
    typingActionService.process(new TypingActionAdapter(typingActionMsg), output);
    this.send(output.get());
  }

 
  private TypingActionMsg toWebsocketMessage(final TypingAction typingAction) {
    final TypingActionMsg msg = new TypingActionMsg();
    msg.setActionTime(typingAction.getActionTime());
    msg.setAuthor(typingAction.getEmitterUsername());
    msg.setChannelName(typingAction.isPrivateChannel() ? null : typingAction.getTarget());
    msg.setTargetUsername(typingAction.isPrivateChannel() ? typingAction.getTarget() : null);
    return msg;
  }

 
  private void send(final TypingActionMsg typingActionMsg) {
    if (typingActionMsg.getTargetUsername() != null) {
      stompMessagingTemplate.sendToUser(typingActionMsg.getTargetUsername(),
          USER_CHAT_QUEUE_DESTINATION, typingActionMsg);
    } else {
      final StompMessageDestination destination = new StompMessageDestination(
          CHAT_TOPIC_DESTINATION_PREFIX.getDestination() + "-" + typingActionMsg.getChannelName());
      stompMessagingTemplate.send(destination, typingActionMsg);
    }
  }

}
