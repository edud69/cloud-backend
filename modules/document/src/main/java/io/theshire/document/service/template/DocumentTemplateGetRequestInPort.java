

package io.theshire.document.service.template;

import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;


public interface DocumentTemplateGetRequestInPort {

 
  String getKey();

 
  DocumentTemplateClassification getClassification();

}
