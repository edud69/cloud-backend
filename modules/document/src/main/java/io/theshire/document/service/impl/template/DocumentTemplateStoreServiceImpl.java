

package io.theshire.document.service.impl.template;

import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;
import io.theshire.common.service.OutPort;
import io.theshire.document.domain.template.DocumentTemplate;
import io.theshire.document.domain.template.DocumentTemplateRepository;
import io.theshire.document.domain.template.email.EmailDocumentTemplate;
import io.theshire.document.service.template.DocumentTemplateGetRequestInPort;
import io.theshire.document.service.template.DocumentTemplateStoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@Transactional
public class DocumentTemplateStoreServiceImpl implements DocumentTemplateStoreService {

 
  @Autowired
  private DocumentTemplateRepository documentTemplateRepository;


  @Override
  public void getTemplate(final DocumentTemplateGetRequestInPort input,
      final OutPort<DocumentTemplate, ?> output) {
    final String key = input.getKey();
    final DocumentTemplateClassification classification = input.getClassification();

    switch (classification) {
      case EMAIL :
        output.receive(documentTemplateRepository.findByClassificationAndTemplateKey(classification,
            key, EmailDocumentTemplate.class));
        break;
      default :
        break;
    }
  }
}
