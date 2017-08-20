

package io.theshire.document.domain.template;

import io.theshire.common.domain.DomainObject;
import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


@Entity
@Table(name = "document_templates")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DocumentTemplate extends DomainObject {

 
  private static final long serialVersionUID = 643311850003664209L;

 
  @Getter
  @Column(name = "template_key")
  private String templateKey;

 
  @Getter
  @Enumerated(EnumType.STRING)
  @Column(name = "classification")
  private DocumentTemplateClassification classification;

 
  public DocumentTemplate(final String templateKey,
      final DocumentTemplateClassification classification) {
    this.templateKey = templateKey;
    this.classification = classification;
  }

 
  protected DocumentTemplate() {

  }

}
