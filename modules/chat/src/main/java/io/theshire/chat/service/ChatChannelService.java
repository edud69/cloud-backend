

package io.theshire.chat.service;

import java.security.Principal;


public interface ChatChannelService {

 
  void join(final Principal principal, final String channelName);

 
  void leave(final Principal principal, final String channelName);

}
