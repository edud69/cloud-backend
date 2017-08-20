

package io.theshire.common.websocket.interceptor;

import io.theshire.common.utils.security.authentication.AuthenticationContext;
import io.theshire.common.utils.security.authentication.AuthenticationInfo;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import java.time.Clock;
import java.time.LocalDateTime;


public class SecuredChannelInterceptor implements ChannelInterceptor {


  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    if (hasExpired()) {
      throw new SecurityException("Token has expired.");
    }

    return message;
  }


  @Override
  public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
  }


  @Override
  public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent,
      Exception ex) {
  }


  @Override
  public boolean preReceive(MessageChannel channel) {
    if (hasExpired()) {
      throw new SecurityException("Token has expired.");
    }

    return true;
  }


  @Override
  public Message<?> postReceive(Message<?> message, MessageChannel channel) {
    return message;
  }


  @Override
  public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
  }

 
  private boolean hasExpired() {
    final AuthenticationInfo authInfo = AuthenticationContext.get();
    return authInfo != null
        ? authInfo.getJwtTokenExpireTime().isBefore(LocalDateTime.now(Clock.systemUTC())) : false;
  }

}
