

package io.theshire.chat.service.impl.listener;

import io.theshire.chat.service.ChatChannelService;
import io.theshire.common.utils.security.authentication.AuthenticationContext;
import io.theshire.common.websocket.listener.WebsocketSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;


@Component
class WebsocketSessionListenerImpl implements WebsocketSessionListener {

 
  private static final String TOPIC_CHAT_PREFIX = "/topic/tenant.?-chat.participants";

 
  @Autowired
  private SimpUserRegistry simpUserRegistry;

 
  @Autowired
  private ChatChannelService chatChannelService;


  @Override
  public void onSessionConnected(Principal principal, long time) {
  }


  @Override
  public void onSessionDisconnected(Principal principal, long time) {
  }


  @Override
  public void onSessionSubscribe(Principal principal, long time, String destination) {
    ensureSecurityContextHolderPresent(principal, () -> {
      final String tenantId = AuthenticationContext.get().getTenantIdentifier();
      final String channelJoinSubDestination = TOPIC_CHAT_PREFIX.replace("?", tenantId);
      if (destination.startsWith(channelJoinSubDestination)) {
        final String channelName =
            destination.replace(channelJoinSubDestination, "").replaceAll("-", "");
        if (simpUserRegistry.getUser(principal.getName()).getSessions().size() == 1) {
          // notify on first connection of the user only
          chatChannelService.join(principal, channelName);
        }
      }
    });
  }


  @Override
  public void onSessionUnsubscribe(Principal principal, long time, String destination) {
    ensureSecurityContextHolderPresent(principal, () -> {
      final String channelJoinSubDestination =
          TOPIC_CHAT_PREFIX.replace("?", AuthenticationContext.get().getTenantIdentifier());
      if (destination.startsWith(channelJoinSubDestination)) {
        final String channelName =
            destination.replace(channelJoinSubDestination, "").replaceAll("-", "");
        if (simpUserRegistry.getUser(principal.getName()).getSessions().size() == 1) {
          // notify only when this is the last session remaining
          chatChannelService.leave(principal, channelName);
        }
      }
    });
  }

 
  private void ensureSecurityContextHolderPresent(final Principal principal,
      final Runnable runnable) {
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      try {
        SecurityContextHolder.getContext().setAuthentication((Authentication) principal);
        runnable.run();
      } finally {
        SecurityContextHolder.getContext().setAuthentication(auth); // restore context
      }
    }
  }

}
