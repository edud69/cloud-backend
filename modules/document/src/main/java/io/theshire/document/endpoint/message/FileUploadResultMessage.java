

package io.theshire.document.endpoint.message;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;



@Data


@EqualsAndHashCode(callSuper = true)
public class FileUploadResultMessage extends TransportMessage {


 
  private static final long serialVersionUID = 169260230286648518L;

 
  private String uploadedDestination;

}
