

package io.theshire.chat.endpoint.websocket;

import io.theshire.chat.endpoint.websocket.message.ParticipantJoinEventMsg;
import io.theshire.chat.service.ChatChannelEventManager;
import io.theshire.common.websocket.endpoint.message.StompMessageDestination;
import io.theshire.common.websocket.endpoint.message.StompMessagingTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
class ChatWebsocketChannelEventEndpoint implements ChatChannelEventManager {

 
  private static final StompMessageDestination TOPIC_CHAT_PARTICIPANT_PREFIX =
      new StompMessageDestination("/topic/tenant.?-chat.participants");

 
  @Autowired
  private StompMessagingTemplate stompMessagingTemplate;


  @Override
  public void join(String channelName, LocalDateTime joinTime, String participantName) {
    final ParticipantJoinEventMsg msg = new ParticipantJoinEventMsg();
    msg.setChannelName(channelName);
    msg.setJoinTime(joinTime);
    msg.setParticipantName(participantName);

    stompMessagingTemplate.send(new StompMessageDestination(
        TOPIC_CHAT_PARTICIPANT_PREFIX.getDestination() + "-" + channelName), msg);
  }


  @Override
  public void leave(String channelName, LocalDateTime joinTime, String participantName) {
    final ParticipantJoinEventMsg msg = new ParticipantJoinEventMsg();
    msg.setChannelName(channelName);
    msg.setJoinTime(joinTime);
    msg.setParticipantName(participantName);

    stompMessagingTemplate.send(new StompMessageDestination(
        TOPIC_CHAT_PARTICIPANT_PREFIX.getDestination() + "-" + channelName), msg);
  }

}
