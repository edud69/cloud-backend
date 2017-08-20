

package io.theshire.common.service.infrastructure.impl.document;

import io.theshire.common.service.infrastructure.bridge.MicroserviceBridgeService;
import io.theshire.common.service.infrastructure.document.DocumentTemplateMessage;
import io.theshire.common.service.infrastructure.document.DocumentTemplateService;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public abstract class DocumentTemplateServiceImpl<T extends DocumentTemplateMessage>
    implements
      DocumentTemplateService<T> {

 
  @Autowired
  private MicroserviceBridgeService crossTalkMicroservicesBridgeService;


  @Override
  public T getTemplate(final String key) {
    try {
      final String encodedKey = URLEncoder.encode(key, "UTF-8");
      final String uri = "/template/" + getDocumentClassification() + "/" + encodedKey + "/";
      return crossTalkMicroservicesBridgeService.invokeRestCall(
          OAuth2ResourceIdentifier.DocumentService, uri, null, HttpMethod.GET, getTargetClass());
    } catch (final UnsupportedEncodingException exc) {
      throw new IllegalArgumentException("Invalid encoded parameters.", exc);
    }
  }

 
  protected abstract Class<T> getTargetClass();

 
  protected abstract String getDocumentClassification();

}
