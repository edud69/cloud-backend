

package io.theshire.common.websocket.service;

import java.time.LocalDateTime;
import java.util.function.Consumer;


public interface WebsocketTokenUpdateService {

 
  void process(final WebsocketTokenUpdateInPort input, final Consumer<LocalDateTime> output);

}
