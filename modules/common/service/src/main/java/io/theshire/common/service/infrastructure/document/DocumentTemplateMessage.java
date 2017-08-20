

package io.theshire.common.service.infrastructure.document;

import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;
import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data


@EqualsAndHashCode(callSuper = true)
public class DocumentTemplateMessage extends TransportMessage {
 
  private static final long serialVersionUID = -292810163960881533L;

 
  private String templateKey;

 
  private DocumentTemplateClassification classification;

}
