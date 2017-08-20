

package io.theshire.common.endpoint;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;


@Data
@EqualsAndHashCode(callSuper = true)
public class ApiError extends TransportMessage {

 
  private static final long serialVersionUID = -3570653568682075421L;

  private String message;

  private String code;

 
  private Map<String, Object> errorParams;

}
