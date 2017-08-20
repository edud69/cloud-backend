

package io.theshire.document.domain.template.email;

import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;
import io.theshire.document.domain.template.DocumentTemplate;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "email_document_templates")
public class EmailDocumentTemplate extends DocumentTemplate {

 
  private static final long serialVersionUID = 3850336529528558605L;

 
  @Column(name = "subject")

 
  @Getter
  private String subject;

 
  @Column(name = "body")

 
  @Getter
  private String body;

 
  public EmailDocumentTemplate(final String subject, final String body, final String templateKey) {
    super(templateKey, DocumentTemplateClassification.EMAIL);
    this.subject = subject;
    this.body = body;
  }

 
  protected EmailDocumentTemplate() {

  }

}
