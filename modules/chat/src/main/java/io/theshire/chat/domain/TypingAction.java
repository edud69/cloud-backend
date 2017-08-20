

package io.theshire.chat.domain;

import io.theshire.common.domain.DomainObject;

import lombok.Getter;

import java.time.Clock;
import java.time.LocalDateTime;


public class TypingAction extends DomainObject {

 
  private static final long serialVersionUID = 2720819948404554623L;

 

 
  @Getter
  private String emitterUsername;

 

 
  @Getter
  private String target;

 
  @Getter
  private boolean privateChannel;

 

 
  @Getter
  private LocalDateTime actionTime;

 
  public TypingAction(final String emitterUsername, final String target, boolean privateChannel) {
    this.emitterUsername = emitterUsername;
    this.target = target;
    this.privateChannel = privateChannel;
    this.actionTime = LocalDateTime.now(Clock.systemUTC());
  }

}
