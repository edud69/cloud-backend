

package io.theshire.common.websocket.endpoint.impl.message;

import io.theshire.common.utils.security.authentication.AuthenticationContext;
import io.theshire.common.utils.transport.message.TransportMessage;
import io.theshire.common.websocket.endpoint.destination.DestinationConstants;
import io.theshire.common.websocket.endpoint.message.StompMessageDestination;
import io.theshire.common.websocket.endpoint.message.StompMessagingTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;


@Service
class StompMessagingTemplateImpl implements StompMessagingTemplate {

 
  private static final Pattern pattern = Pattern.compile("/tenant\\.\\?-");

 
  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;


  @Override
  public void send(StompMessageDestination destination, TransportMessage payload) {
    final String completeDestination = tenantAwareDestination(destination.getDestination());
    simpMessagingTemplate.convertAndSend(completeDestination, payload);
  }


  @Override
  public void sendToUser(String username, StompMessageDestination destination,
      TransportMessage payload) {
    final String completeDestination = tenantAwareDestination(destination.getDestination());
    simpMessagingTemplate.convertAndSendToUser(username, completeDestination, payload);
  }

 
  private String tenantAwareDestination(final String destination) {
    if (destination.contains(DestinationConstants.TENANT_ROUTE_PREFIX)) {
      final String tenantId = AuthenticationContext.get().getTenantIdentifier();
      final String tenantSegment = DestinationConstants.TENANT_ROUTE_PREFIX + tenantId + "-";
      return pattern.matcher(destination).replaceFirst(tenantSegment);
    } else {
      return destination;
    }
  }

}
