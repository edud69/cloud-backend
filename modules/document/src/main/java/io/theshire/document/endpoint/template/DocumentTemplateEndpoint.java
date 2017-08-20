

package io.theshire.document.endpoint.template;

import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;
import io.theshire.common.endpoint.ManagedRestEndpoint;
import io.theshire.common.service.OutPort;
import io.theshire.common.service.infrastructure.document.DocumentTemplateMessage;
import io.theshire.common.service.infrastructure.email.EmailTemplateMessage;
import io.theshire.document.domain.template.DocumentTemplate;
import io.theshire.document.domain.template.email.EmailDocumentTemplate;
import io.theshire.document.service.template.DocumentTemplateGetRequestInPort;
import io.theshire.document.service.template.DocumentTemplateStoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/template", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentTemplateEndpoint extends ManagedRestEndpoint {

 
  @Autowired
  private DocumentTemplateStoreService documentTemplateStoreService;

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.GET, value = "/{classification}/{templateKey}/")
  public ResponseEntity<DocumentTemplateMessage> httpGet(
      @PathVariable("templateKey") final String templateKey,
      @PathVariable("classification") final String classification) {
    final DocumentTemplateGetRequestInPort input = buildGetRequest(templateKey, classification);
    final OutPort<DocumentTemplate, DocumentTemplateMessage> output =
        OutPort.create(received -> toRest(received));
    documentTemplateStoreService.getTemplate(input, output);
    return buildResponse(output.get());
  }

 
  private DocumentTemplateMessage toRest(final DocumentTemplate template) {
    switch (template.getClassification()) {
      case EMAIL :
        final EmailDocumentTemplate emailTemplate = (EmailDocumentTemplate) template;
        final EmailTemplateMessage message = new EmailTemplateMessage();
        message.setBody(emailTemplate.getBody());
        message.setClassification(emailTemplate.getClassification());
        message.setSubject(emailTemplate.getSubject());
        message.setTemplateKey(emailTemplate.getTemplateKey());
        return message;
      default :
        break;
    }
    return null;
  }

 
  private DocumentTemplateGetRequestInPort buildGetRequest(final String templateKey,
      final String classification) {
    return new DocumentTemplateGetRequestInPort() {

      @Override
      public String getKey() {
        return templateKey;
      }

      @Override
      public DocumentTemplateClassification getClassification() {
        return DocumentTemplateClassification.fromClassification(classification);
      }
    };
  }

}
