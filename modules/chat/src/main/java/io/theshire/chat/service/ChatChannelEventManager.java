

package io.theshire.chat.service;

import java.time.LocalDateTime;


public interface ChatChannelEventManager {

 
  void join(final String channelName, LocalDateTime joinTime, final String participantName);

 
  void leave(final String channelName, LocalDateTime joinTime, final String participantName);

}
