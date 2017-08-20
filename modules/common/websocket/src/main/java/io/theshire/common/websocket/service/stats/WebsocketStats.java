

package io.theshire.common.websocket.service.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpSubscriptionMatcher;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

import java.util.HashSet;
import java.util.Set;


@Component
public class WebsocketStats {

 
  @Autowired
  private SimpUserRegistry simpUserRegistry;

 
  @Autowired
  private WebSocketMessageBrokerStats webSocketMessageBrokerStats;

 
  public String getWebsocketStatsInfo() {
    return webSocketMessageBrokerStats.getWebSocketSessionStatsInfo();
  }

 
  public String getStompBrokerRelayStatsInfo() {
    return webSocketMessageBrokerStats.getStompBrokerRelayStatsInfo();
  }

 
  public long getSubscribedSessionCount(final String destination) {
    return getSubscribedCount(destination, subcription -> true);
  }

 
  public long getSubscribedUserCount(final String destination) {
    final Set<String> subUsers = new HashSet<>();
    final SimpSubscriptionMatcher matcher = subscription -> {
      final String subUser = subscription.getSession().getUser().getName();
      if (!subUsers.contains(subUser)) {
        subUsers.add(subUser);
        if (subscription.getDestination().equals(destination)) {
          return true;
        }
      }
      return false;
    };

    return getSubscribedCount(destination, matcher);
  }

 
  public long getSubscribedCount(final String destination, SimpSubscriptionMatcher matcher) {
    return simpUserRegistry.findSubscriptions(matcher).size();
  }

}
