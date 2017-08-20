

package io.theshire.common.service.infrastructure.email;

import io.theshire.common.service.infrastructure.document.DocumentTemplateMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data


@EqualsAndHashCode(callSuper = true)
public class EmailTemplateMessage extends DocumentTemplateMessage {

 
  private static final long serialVersionUID = -5981758099076217102L;

 
  private String subject;

 
  private String body;

}
