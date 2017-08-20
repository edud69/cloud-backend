

package io.theshire.common.domain.document.template.classification;

import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;

import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;


public class DocumentTemplateClassificationTest {

 
  @Test(expected = NoSuchElementException.class)
  public void testNullFromClassification() throws Exception {
    DocumentTemplateClassification.fromClassification(null);
  }

 
  @Test(expected = NoSuchElementException.class)
  public void testInvalidFromClassification() throws Exception {
    DocumentTemplateClassification.fromClassification("non-existing-value");
  }

 
  @Test
  public void testValidFromClassification() throws Exception {
    Assert.assertEquals(DocumentTemplateClassification.EMAIL.getClassification(),
        DocumentTemplateClassification
            .fromClassification(DocumentTemplateClassification.EMAIL.getClassification())
            .getClassification());
    Assert.assertEquals(DocumentTemplateClassification.EMAIL.toString(),
        DocumentTemplateClassification
            .fromClassification(DocumentTemplateClassification.EMAIL.getClassification())
            .toString());
  }

}
