

package io.theshire.common.websocket.endpoint.security;

import io.theshire.common.utils.security.authentication.AuthenticationContext;
import io.theshire.common.websocket.endpoint.destination.DestinationConstants;
import io.theshire.common.websocket.endpoint.message.MessageHeaderExtractor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.access.expression.DefaultMessageSecurityExpressionHandler;
import org.springframework.stereotype.Component;




@Slf4j
@Component("websocketSubscribeDestinationAccessRouter")
class WebsocketSubscribeDestAccessRouter extends DefaultMessageSecurityExpressionHandler<Object> {

 
  public boolean checkAccess(final Authentication auth, final Message<Object> message) {
    return evaluate(auth, MessageHeaderExtractor.extractDestination(message));
  }

 
  private boolean evaluate(final Authentication auth, final String destination) {
    // validates tenant access
    if (destination.contains(DestinationConstants.TENANT_ROUTE_PREFIX)) {
      String tenantId =
          destination.substring(destination.indexOf(DestinationConstants.TENANT_ROUTE_PREFIX)
              + DestinationConstants.TENANT_ROUTE_PREFIX.length());
      final int idx = tenantId.indexOf("-");
      if (idx == -1) {
        throw new MessagingException("Destination does not contain a tenant.");
      }

      tenantId = tenantId.substring(0, idx);
      if (!tenantId.equals(AuthenticationContext.get().getTenantIdentifier())) {
        return false;
      }
    }

    if (destination.startsWith(DestinationConstants.DESTINATION_USER_PREFIX)) {
      return true;
    } else if (destination.startsWith(DestinationConstants.DESTINATION_QUEUE_PREFIX)) {
      return isSubAllowedToQueue(auth, destination);
    } else if (destination.startsWith(DestinationConstants.DESTINATION_TOPIC_PREFIX)) {
      return isSubAllowedToTopic(auth, destination);
    } else if (destination.startsWith(DestinationConstants.DESTINATION_APP_PREFIX)) {
      return true;// This should be handled by the logic in the controllers
    }

    log.debug("Destination was not handled, subscription will be denied.");

    return false;
  }

 
  private boolean isSubAllowedToTopic(final Authentication auth, final String destination) {
    log.debug("Validating subscription access to destination={} for auth={}.", destination, auth);
    return true;
  }

 
  private boolean isSubAllowedToQueue(final Authentication auth, final String destination) {
    log.debug("Validating subscription access to destination={} for auth={}.", destination, auth);
    return true;
  }
}