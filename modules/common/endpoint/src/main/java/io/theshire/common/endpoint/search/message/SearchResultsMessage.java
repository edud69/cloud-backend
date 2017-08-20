

package io.theshire.common.endpoint.search.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;




@Data


@EqualsAndHashCode(callSuper = true)
public class SearchResultsMessage extends TransportMessage {

 
  private static final long serialVersionUID = 8979479562366893677L;

 
  private List<? extends TransportMessage> results;

 
  private Long totalResultsCount;

 
  private Long queryExecutionTimeInMillis;

}
