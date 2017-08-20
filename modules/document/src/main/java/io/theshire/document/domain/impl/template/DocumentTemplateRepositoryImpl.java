

package io.theshire.document.domain.impl.template;

import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;
import io.theshire.document.domain.template.DocumentTemplate;
import io.theshire.document.domain.template.DocumentTemplateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Transactional
@Service
public class DocumentTemplateRepositoryImpl implements DocumentTemplateRepository {

 
  @Autowired
  private DocumentTemplateJpaRepository documentTemplateJpaRepository;


  @Override
  public DocumentTemplate findById(Long id) {
    return documentTemplateJpaRepository.findOne(id);
  }


  @Override
  public DocumentTemplate save(DocumentTemplate domainObject) {
    return documentTemplateJpaRepository.save(domainObject);
  }


  @Override
  public <T extends DocumentTemplate> T findByClassificationAndTemplateKey(
      DocumentTemplateClassification classification, String templateKey,
      final Class<T> targetType) {
    return targetType.cast(documentTemplateJpaRepository
        .findByClassificationAndTemplateKey(classification, templateKey));
  }

}
