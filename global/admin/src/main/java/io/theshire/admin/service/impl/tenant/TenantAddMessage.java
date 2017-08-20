

package io.theshire.admin.service.impl.tenant;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Getter;
import lombok.Setter;




@Getter


@Setter
public class TenantAddMessage extends TransportMessage {

 
  private static final long serialVersionUID = 5261310946250204016L;

 
  private String tenantId;

 
  private String tenantState = "CREATION_PENDING";

 
  private TenantSqlEndpoint sqlEndpoint;

}
