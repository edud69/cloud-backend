

package io.theshire.chat.service.impl;

import io.theshire.chat.domain.ChatGroupMessage;
import io.theshire.chat.domain.ChatPrivateMessage;
import io.theshire.chat.service.ChatSendGroupInPort;
import io.theshire.chat.service.ChatSendPrivateInPort;
import io.theshire.chat.service.ChatService;
import io.theshire.common.service.OutPort;
import io.theshire.common.utils.security.authentication.AuthenticationContext;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;


@Service
class ChatServiceImpl implements ChatService {


  @Override
  public void send(final ChatSendPrivateInPort input, final OutPort<ChatPrivateMessage, ?> output) {
    final ChatPrivateMessage toSend =
        new ChatPrivateMessage(AuthenticationContext.get().getUsername(), input.getTargetUsername(),
            input.getChat(), LocalDateTime.now(Clock.systemUTC()));
    // TODO Archive message in db + sanitize
    output.receive(toSend);
  }


  @Override
  public void send(final ChatSendGroupInPort input, final OutPort<ChatGroupMessage, ?> output) {
    final ChatGroupMessage toSend = new ChatGroupMessage(AuthenticationContext.get().getUsername(),
        input.getChannelName(), input.getChat(), LocalDateTime.now(Clock.systemUTC()));
    // TODO Archive message in db + sanitize
    output.receive(toSend);
  }

}
