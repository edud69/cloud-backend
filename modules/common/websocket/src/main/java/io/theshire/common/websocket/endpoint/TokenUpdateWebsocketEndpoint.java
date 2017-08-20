

package io.theshire.common.websocket.endpoint;

import io.theshire.common.utils.security.authentication.AuthenticationContext;
import io.theshire.common.websocket.endpoint.message.StompMessageDestination;
import io.theshire.common.websocket.endpoint.message.StompMessagingTemplate;
import io.theshire.common.websocket.endpoint.message.TokenUpdateRequestMsg;
import io.theshire.common.websocket.endpoint.message.TokenUpdateResponseMsg;
import io.theshire.common.websocket.service.WebsocketTokenUpdateInPort;
import io.theshire.common.websocket.service.WebsocketTokenUpdateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.function.Consumer;


@Controller
class TokenUpdateWebsocketEndpoint {

 
  private static final StompMessageDestination TOKEN_UPDATE_QUEUE_NAME =
      new StompMessageDestination("/queue/tenant.?-token.update");

 
  @Autowired
  private WebsocketTokenUpdateService websocketTokenUpdateService;

 
  @Autowired
  private StompMessagingTemplate stompMessagingTemplate;

 
  @MessageMapping("/token.update")
  public void updateToken(final TokenUpdateRequestMsg tokenUpdateRequestMsg,
      final MessageHeaders headers) {
    final WebsocketTokenUpdateInPort input = buildInputPort(tokenUpdateRequestMsg, headers);
    final Consumer<LocalDateTime> output = updateTime -> this.sendResponse(updateTime);
    websocketTokenUpdateService.process(input, output);
  }

 
  private void sendResponse(final LocalDateTime updateTime) {
    final TokenUpdateResponseMsg response = new TokenUpdateResponseMsg();
    response.setTokenUpdateTime(updateTime);
    stompMessagingTemplate.sendToUser(AuthenticationContext.get().getUsername(),
        TOKEN_UPDATE_QUEUE_NAME, response);
  }

 
  private WebsocketTokenUpdateInPort buildInputPort(final TokenUpdateRequestMsg request,
      final MessageHeaders headers) {
    return new WebsocketTokenUpdateInPort() {

      @Override
      public String getToken() {
        return request.getNewTokenValue();
      }

      @Override
      public MessageHeaders getHeaders() {
        return headers;
      }
    };
  }

}
