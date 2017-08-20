

package io.theshire.common.service.infrastructure.bridge;

import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;

import org.springframework.http.HttpMethod;

import java.util.Map;


public interface MicroserviceBridgeService {

 
  <T> T invokeRestCall(final OAuth2ResourceIdentifier targetedService, final String uriPath,
      Map<String, String> additionalHeader, final HttpMethod method,
      final Class<T> targetReturnType);

 
  <T> T invokeRestCall(final OAuth2ResourceIdentifier targetedService, final String uriPath,
      Map<String, String> additionalHeader, final HttpMethod method, final Object jsonPayload,
      final Class<T> targetReturnType);

 
  <T> T invokeRestCall(final OAuth2ResourceIdentifier targetedService, final String uriPath,
      Map<String, String> additionalHeader, final HttpMethod method,
      final Map<String, String> parameters, final Class<T> targetReturnType);

}
