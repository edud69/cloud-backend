

package io.theshire.document.service.template;

import io.theshire.common.service.OutPort;
import io.theshire.common.utils.security.permission.constants.SecurityDocumentMicroservicePermissionConstants;
import io.theshire.document.domain.template.DocumentTemplate;

import org.springframework.security.access.prepost.PreAuthorize;


public interface DocumentTemplateStoreService {

 
  @PreAuthorize("hasPermission(#key, '"
      + SecurityDocumentMicroservicePermissionConstants.DOCUMENT_TEMPLATE_READ + "')")
  void getTemplate(final DocumentTemplateGetRequestInPort input,
      final OutPort<DocumentTemplate, ?> output);

}
