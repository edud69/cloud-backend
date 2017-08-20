

package io.theshire.document.domain.template.email;

import org.junit.Assert;
import org.junit.Test;


public class EmailDocumentTemplateTest {

 
  @Test
  public void ctorsTest() {
    final EmailDocumentTemplate protectedCtor = new EmailDocumentTemplate();
    Assert.assertNull(protectedCtor.getBody());
    Assert.assertNull(protectedCtor.getSubject());

    final EmailDocumentTemplate publicCtor =
        new EmailDocumentTemplate("Subject", "Body", "TemplateKey");
    Assert.assertEquals("Subject", publicCtor.getSubject());
    Assert.assertEquals("Body", publicCtor.getBody());
  }

}
