

package io.theshire.document.domain.impl.template;

import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;
import io.theshire.common.domain.impl.repository.JpaSpringRepository;
import io.theshire.document.domain.template.DocumentTemplate;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.repository.query.Param;


public interface DocumentTemplateJpaRepository extends JpaSpringRepository<DocumentTemplate> {

 
  @Query("SELECT dt FROM DocumentTemplate dt "
      + "WHERE templateKey = :templateKey AND classification = :classification")
  DocumentTemplate findByClassificationAndTemplateKey(
      @Param("classification") final DocumentTemplateClassification classification,
      @Param("templateKey") final String templateKey);

}
