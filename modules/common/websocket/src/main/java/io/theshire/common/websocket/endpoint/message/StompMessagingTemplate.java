

package io.theshire.common.websocket.endpoint.message;

import io.theshire.common.utils.transport.message.TransportMessage;


public interface StompMessagingTemplate {

 
  void send(final StompMessageDestination destination, final TransportMessage payload);

 
  void sendToUser(final String username, final StompMessageDestination destination,
      final TransportMessage payload);

}
