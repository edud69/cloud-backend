

package io.theshire.common.server.tenant.manager;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TenantStateChangeMessage extends TransportMessage {

 
  public static enum TenantState {

   
    CREATION_PENDING,

   
    CREATED,

   
    REMOVAL_PENDING,

   
    REMOVED
  }

 
  private static final long serialVersionUID = 2535311916028333526L;

 
  private String tenantId;

 
  private TenantState tenantState;

 
  private TenantSqlEndpointMessage sqlEndpoint;

}
