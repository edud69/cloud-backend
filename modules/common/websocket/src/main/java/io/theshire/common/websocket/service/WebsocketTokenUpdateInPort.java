

package io.theshire.common.websocket.service;

import org.springframework.messaging.MessageHeaders;


public interface WebsocketTokenUpdateInPort {

 
  String getToken();

 
  MessageHeaders getHeaders();

}
