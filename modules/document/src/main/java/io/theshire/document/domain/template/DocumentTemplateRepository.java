

package io.theshire.document.domain.template;

import io.theshire.common.domain.DomainRepository;
import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;


public interface DocumentTemplateRepository extends DomainRepository<DocumentTemplate> {

 
  <T extends DocumentTemplate> T findByClassificationAndTemplateKey(
      final DocumentTemplateClassification classification, final String templateKey,
      final Class<T> targetType);

}
