

package io.theshire.chat.service.impl;

import io.theshire.chat.service.ChatChannelEventManager;
import io.theshire.chat.service.ChatChannelService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Clock;
import java.time.LocalDateTime;


@Slf4j
@Service
class ChatChannelServiceImpl implements ChatChannelService {

  @Autowired
  private ChatChannelEventManager chatChannelEventManager;


  @Override
  public void join(Principal principal, String channelName) {
    // 1. TODO Check user as access... otherwise throw
    log.debug("User {} has joined the chat channel {}.", principal.getName(), channelName);
    chatChannelEventManager.join(channelName, LocalDateTime.now(Clock.systemUTC()),
        principal.getName());
  }


  @Override
  public void leave(Principal principal, String channelName) {
    log.debug("User {} has leaved the chat channel {}.", principal.getName(), channelName);
    this.chatChannelEventManager.leave(channelName, LocalDateTime.now(Clock.systemUTC()),
        principal.getName());
  }

}
