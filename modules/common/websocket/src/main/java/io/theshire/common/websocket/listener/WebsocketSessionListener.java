

package io.theshire.common.websocket.listener;

import java.security.Principal;


public interface WebsocketSessionListener {

 
  void onSessionConnected(final Principal principal, final long time);

 
  void onSessionDisconnected(final Principal principal, final long time);

 
  void onSessionSubscribe(final Principal principal, final long time, final String destination);

 
  void onSessionUnsubscribe(final Principal principal, final long time, final String destination);

}
