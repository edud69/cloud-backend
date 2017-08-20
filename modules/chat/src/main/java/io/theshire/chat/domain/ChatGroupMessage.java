

package io.theshire.chat.domain;

import lombok.Getter;

import java.time.LocalDateTime;


public class ChatGroupMessage extends ChatMessage {

 
  private static final long serialVersionUID = -7390830332495110789L;

 
  @Getter
  private String channelName;

 
  public ChatGroupMessage(final String emitterUsername, final String channelName, final String chat,
      final LocalDateTime sentTime) {
    super(emitterUsername, sentTime, chat);
    this.channelName = channelName;
  }

 
  protected ChatGroupMessage() {

  }

}
