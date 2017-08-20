

package io.theshire.common.service.infrastructure.impl.email;

import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;
import io.theshire.common.service.infrastructure.email.EmailTemplateMessage;
import io.theshire.common.service.infrastructure.email.EmailTemplateService;
import io.theshire.common.service.infrastructure.impl.document.DocumentTemplateServiceImpl;

import org.springframework.stereotype.Service;


@Service
public class EmailTemplateServiceImpl extends DocumentTemplateServiceImpl<EmailTemplateMessage>
    implements
      EmailTemplateService {


  @Override
  protected Class<EmailTemplateMessage> getTargetClass() {
    return EmailTemplateMessage.class;
  }


  @Override
  protected String getDocumentClassification() {
    return DocumentTemplateClassification.EMAIL.getClassification();
  }

}
