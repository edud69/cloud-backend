

package io.theshire.chat.domain;

import lombok.Getter;

import java.time.LocalDateTime;


public class ChatPrivateMessage extends ChatMessage {

 
  private static final long serialVersionUID = -7061286622648559465L;

 
  @Getter
  private String targetUsername;

 
  public ChatPrivateMessage(final String emitterUsername, final String targetUsername,
      final String chat, final LocalDateTime sentTime) {
    super(emitterUsername, sentTime, chat);
    this.targetUsername = targetUsername;
  }

 
  protected ChatPrivateMessage() {

  }

}
