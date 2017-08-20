

package io.theshire.admin.service.impl.tenant;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Getter;
import lombok.Setter;



@Getter


@Setter
public class TenantRemoveMessage extends TransportMessage {


 
  private static final long serialVersionUID = -8578272434885345918L;

 
  private String tenantId;

 
  private String tenantState = "REMOVAL_PENDING";

}
