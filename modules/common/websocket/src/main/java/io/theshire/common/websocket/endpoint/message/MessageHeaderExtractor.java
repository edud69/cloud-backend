

package io.theshire.common.websocket.endpoint.message;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

import java.util.List;
import java.util.Map;


public class MessageHeaderExtractor {

 
  public static final String HEADER_DESTINATION = "destination";

 
  public static final String HEADER_SUB_ID = "id";

 
  public static final String HEADER_NATIVE = "nativeHeaders";

 
  public static final String HEADER_SESSION_ID = "simpSessionId";

 
  public static String extractSessionId(final MessageHeaders headers) {
    if (headers == null) {
      return null;
    }

    final String sessionId = (String) headers.get(HEADER_SESSION_ID);
    if (sessionId == null) {
      throw new MessagingException("A stomp message must have a sessionId.");
    }

    return sessionId;
  }

 
  public static Map<String, Object> extractNativeHeaders(final MessageHeaders headers) {
    @SuppressWarnings("unchecked")
    final Map<String, Object> nativeHeaders = (Map<String, Object>) headers.get(HEADER_NATIVE);
    if (nativeHeaders == null) {
      throw new MessagingException("A stomp message must have nativeHeaders.");
    }

    return nativeHeaders;
  }

 
  public static Map<String, Object> extractNativeHeaders(final Message<?> message) {
    return extractNativeHeaders(message.getHeaders());
  }

 
  public static String extractDestination(final Message<?> message) {
    final Map<String, Object> nativeHeaders = extractNativeHeaders(message);

    @SuppressWarnings("unchecked")
    final List<String> dests = (List<String>) nativeHeaders.get(HEADER_DESTINATION);
    if (dests == null) {
      return null;
    }

    if (dests.size() != 1) {
      throw new MessagingException("Make sure you subscribe to only one destination at a time.");
    }

    final String destination = dests.get(0);
    if (destination == null) {
      throw new MessagingException("A stomp message must have a destination.");
    }

    return destination;
  }

 
  public static String extractUnsubscriptionId(final Message<?> message) {
    final Map<String, Object> nativeHeaders = extractNativeHeaders(message);

    @SuppressWarnings("unchecked")
    final List<String> subIds = (List<String>) nativeHeaders.get(HEADER_SUB_ID);
    if (subIds == null) {
      return null;
    }

    if (subIds.size() != 1) {
      throw new MessagingException("Make sure you subscribe to only one destination at a time.");
    }

    final String unsubId = subIds.get(0);
    if (unsubId == null) {
      throw new MessagingException("A stomp UNSUBSCRIBE message must have a unsubscription id.");
    }

    return unsubId;
  }

}
