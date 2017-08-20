

package io.theshire.common.domain.document.template.classification;

import lombok.Getter;

import java.util.Arrays;


public enum DocumentTemplateClassification {

 
  EMAIL("email");

 
  @Getter
  private final String classification;

 
  private DocumentTemplateClassification(final String classification) {
    this.classification = classification;
  }

 
  public static final DocumentTemplateClassification fromClassification(
      final String classification) {
    return Arrays.asList(DocumentTemplateClassification.values()).stream()
        .filter(e -> e.getClassification().equals(classification)).findAny().get();
  }


  @Override
  public String toString() {
    return classification;
  }

}
