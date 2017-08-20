

package io.theshire.common.websocket.endpoint.message;

import io.theshire.common.websocket.endpoint.destination.DestinationConstants;

import lombok.Getter;

import org.springframework.messaging.MessagingException;


public class StompMessageDestination {

 
  @Getter
  private final String destination;

 
  public StompMessageDestination(final String destination) {
    String routingKey = null;
    String toEvaluate = destination;

    if (toEvaluate.startsWith(DestinationConstants.DESTINATION_USER_PREFIX)) {
      toEvaluate =
          toEvaluate.replace(DestinationConstants.DESTINATION_USER_PREFIX, "").replace("/", "");
    }

    if (toEvaluate.startsWith(DestinationConstants.DESTINATION_TOPIC_PREFIX)) {
      routingKey =
          toEvaluate.replace(DestinationConstants.DESTINATION_TOPIC_PREFIX, "").replace("/", "");
    } else if (toEvaluate.startsWith(DestinationConstants.DESTINATION_QUEUE_PREFIX)) {
      routingKey =
          toEvaluate.replace(DestinationConstants.DESTINATION_QUEUE_PREFIX, "").replace("/", "");
    } else {
      throw new MessagingException(
          "Stomp destination to the broker can only starts with /topic/ or /queue/.");
    }

    if (routingKey.contains("/")) {
      throw new MessagingException("Stomp destination cannot contain a routing key with a '/'. "
          + "It must be for example '/topic/routing.key'.");
    }

    this.destination = destination;
  }


  @Override
  public String toString() {
    return this.destination;
  }

}
