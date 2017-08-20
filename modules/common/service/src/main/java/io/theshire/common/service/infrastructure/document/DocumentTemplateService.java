

package io.theshire.common.service.infrastructure.document;

import io.theshire.common.utils.security.permission.constants.SecurityDocumentMicroservicePermissionConstants;

import org.springframework.security.access.prepost.PreAuthorize;


public interface DocumentTemplateService<T extends DocumentTemplateMessage> {

 
  @PreAuthorize("hasPermission(#key, '"
      + SecurityDocumentMicroservicePermissionConstants.DOCUMENT_TEMPLATE_READ + "')")
  T getTemplate(final String key);

}
