

package io.theshire.chat.domain;

import io.theshire.common.domain.DomainObject;

import lombok.Getter;

import java.time.LocalDateTime;


public abstract class ChatMessage extends DomainObject {

 
  private static final long serialVersionUID = 8361686253946458771L;

 
  @Getter
  private String chat;

 
  @Getter
  private String emitterUsername;

 
  @Getter
  private LocalDateTime sentTime;

 
  public ChatMessage(final String emitterUsername, final LocalDateTime sentTime,
      final String chat) {
    this.chat = chat;
    this.emitterUsername = emitterUsername;
    this.sentTime = sentTime;
  }

 
  protected ChatMessage() {

  }

}
