

package io.theshire.common.service.infrastructure.impl.indexation;

import io.theshire.common.service.infrastructure.indexation.IndexationProcessor.IndexationProcessState;
import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class IndexationStateMessage extends TransportMessage {


 
  private static final long serialVersionUID = 5440611110662174090L;

 
  private String tenantId;

 
  private String indexingType;

 
  private IndexationProcessState state;

}
