

package io.theshire.chat.endpoint.websocket;

import io.theshire.chat.domain.ChatGroupMessage;
import io.theshire.chat.domain.ChatPrivateMessage;
import io.theshire.chat.endpoint.websocket.adapters.ChatGroupMessageSendAdapter;
import io.theshire.chat.endpoint.websocket.adapters.ChatPrivateMessageSendAdapter;
import io.theshire.chat.endpoint.websocket.message.ChatGroupMsg;
import io.theshire.chat.endpoint.websocket.message.ChatPrivateMsg;
import io.theshire.chat.service.ChatSendPrivateInPort;
import io.theshire.chat.service.ChatService;
import io.theshire.common.service.OutPort;
import io.theshire.common.websocket.endpoint.message.StompMessageDestination;
import io.theshire.common.websocket.endpoint.message.StompMessagingTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


@Controller
public class ChatWebsocketEndpoint {

 
  private static final StompMessageDestination USER_CHAT_QUEUE_DESTINATION =
      new StompMessageDestination("/queue/tenant.?-chat");

 
  private static final StompMessageDestination CHAT_TOPIC_DESTINATION_PREFIX =
      new StompMessageDestination("/topic/tenant.?-chat");

 
  @Autowired
  private StompMessagingTemplate stompMessagingTemplate;

 
  @Autowired
  private ChatService chatService;

 
  @MessageMapping("/chat.group.message")
  public void sendChat(@Payload final ChatGroupMsg chatMsg) {
    final OutPort<ChatGroupMessage, ChatGroupMsg> output =
        OutPort.create(received -> toWebsocketMessage(received));
    chatService.send(new ChatGroupMessageSendAdapter(chatMsg), output);
    this.send(output.get());
  }

 
  @MessageMapping("/chat.private.message")
  public void sendPrivateChat(@Payload final ChatPrivateMsg chatMsg) {
    final ChatSendPrivateInPort input = new ChatPrivateMessageSendAdapter(chatMsg);
    final OutPort<ChatPrivateMessage, ChatPrivateMsg> output =
        OutPort.create(received -> toWebsocketMessage(received));
    chatService.send(input, output);
    this.send(output.get());
  }

 
  private ChatGroupMsg toWebsocketMessage(final ChatGroupMessage received) {
    final ChatGroupMsg chatGroupMsg = new ChatGroupMsg();
    chatGroupMsg.setMessage(received.getChat());
    chatGroupMsg.setSenderUsername(received.getEmitterUsername());
    chatGroupMsg.setSentTime(received.getSentTime());
    chatGroupMsg.setChannelName(received.getChannelName());
    return chatGroupMsg;
  }

 
  private ChatPrivateMsg toWebsocketMessage(final ChatPrivateMessage received) {
    final ChatPrivateMsg chatPrivateMsg = new ChatPrivateMsg();
    chatPrivateMsg.setMessage(received.getChat());
    chatPrivateMsg.setSenderUsername(received.getEmitterUsername());
    chatPrivateMsg.setSentTime(received.getSentTime());
    chatPrivateMsg.setTargetUsername(received.getTargetUsername());
    return chatPrivateMsg;
  }

 
  private void send(final ChatPrivateMsg chat) {
    stompMessagingTemplate.sendToUser(chat.getTargetUsername(), USER_CHAT_QUEUE_DESTINATION, chat);
  }

 
  private void send(final ChatGroupMsg chatMsg) {
    final StompMessageDestination destination = new StompMessageDestination(
        CHAT_TOPIC_DESTINATION_PREFIX.getDestination() + "-" + chatMsg.getChannelName());
    stompMessagingTemplate.send(destination, chatMsg);
  }
}
