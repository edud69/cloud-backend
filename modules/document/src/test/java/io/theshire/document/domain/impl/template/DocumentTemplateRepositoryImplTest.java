

package io.theshire.document.domain.impl.template;

import io.theshire.common.domain.document.template.classification.DocumentTemplateClassification;
import io.theshire.document.domain.template.DocumentTemplate;
import io.theshire.document.domain.template.email.EmailDocumentTemplate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DocumentTemplateRepositoryImplTest {

 
  @Mock
  private DocumentTemplateJpaRepository documentTemplateJpaRepository;

 
  @InjectMocks
  private final DocumentTemplateRepositoryImpl classUnderTest =
      new DocumentTemplateRepositoryImpl();

 
  @Test
  public void findByIdTest() {
    final Long id = -1L;
    final DocumentTemplate expected = Mockito.mock(DocumentTemplate.class);
    Mockito.when(documentTemplateJpaRepository.findOne(id)).thenReturn(expected);
    final DocumentTemplate actual = classUnderTest.findById(id);
    Mockito.verify(documentTemplateJpaRepository).findOne(id);
    Assert.assertEquals(expected, actual);
  }

 
  @Test
  public void saveTest() {
    final DocumentTemplate initial = Mockito.mock(DocumentTemplate.class);
    final DocumentTemplate expected = Mockito.mock(DocumentTemplate.class);
    Mockito.when(documentTemplateJpaRepository.save(initial)).thenReturn(expected);
    final DocumentTemplate actual = classUnderTest.save(initial);
    Mockito.verify(documentTemplateJpaRepository).save(initial);
    Assert.assertEquals(expected, actual);
  }

 
  @Test
  public void testFindByClassificationAndTemplateKey() throws Exception {
    final DocumentTemplateClassification classification = DocumentTemplateClassification.EMAIL;
    final String templateKey = "template-key";
    final Class<EmailDocumentTemplate> targetType = EmailDocumentTemplate.class;
    final EmailDocumentTemplate result = Mockito.mock(EmailDocumentTemplate.class);
    Mockito.when(documentTemplateJpaRepository.findByClassificationAndTemplateKey(classification,
        templateKey)).thenReturn(result);
    final EmailDocumentTemplate returnResult =
        classUnderTest.findByClassificationAndTemplateKey(classification, templateKey, targetType);
    Mockito.verify(documentTemplateJpaRepository).findByClassificationAndTemplateKey(classification,
        templateKey);
    Assert.assertEquals(result, returnResult);
  }

}
