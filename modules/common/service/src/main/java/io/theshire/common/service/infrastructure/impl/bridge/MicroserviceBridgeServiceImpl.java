

package io.theshire.common.service.infrastructure.impl.bridge;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import io.theshire.common.service.infrastructure.bridge.MicroserviceBridgeService;
import io.theshire.common.utils.http.constants.HttpHeaderConstants;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;
import io.theshire.common.utils.security.authentication.AuthenticationContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
public class MicroserviceBridgeServiceImpl implements MicroserviceBridgeService {

 
  @Autowired
  private RestTemplate restTemplate;

 
  private String url(OAuth2ResourceIdentifier targetedService, String uriPath) {
    final StringBuilder sb = new StringBuilder();
    sb.append("http://");
    sb.append(targetedService.getMicroserviceName());
    sb.append(uriPath.startsWith("/") ? "" : "/");
    sb.append(uriPath);
    return sb.toString();
  }

 
  private HttpHeaders headers(Map<String, String> additionalHeaders) {
    final HttpHeaders headers = new HttpHeaders();
    boolean containsAuthHeader = false;

    if (additionalHeaders != null) {
      additionalHeaders.entrySet().forEach(e -> headers.add(e.getKey(), e.getValue()));
      containsAuthHeader =
          additionalHeaders.keySet().contains(HttpHeaderConstants.HTTP_HEADER_authorization);
    }

    if (!containsAuthHeader) {
      final String token = AuthenticationContext.get().getJwtToken();
      if (token != null) {
        headers.add(HttpHeaderConstants.HTTP_HEADER_authorization,
            new StringBuilder().append("Bearer ").append(token).toString());
      }
    }

    return headers;
  }


  @Override
  @HystrixCommand()
  public <T> T invokeRestCall(OAuth2ResourceIdentifier targetedService, String uriPath,
      Map<String, String> additionalHeader, HttpMethod method, Class<T> targetReturnType) {
    final String url = url(targetedService, uriPath);
    final HttpHeaders headers = headers(additionalHeader);
    final HttpEntity<?> httpEntity = new HttpEntity<Object>(null, headers);
    return restTemplate.exchange(url, method, httpEntity, targetReturnType).getBody();
  }


  @Override
  @HystrixCommand()
  public <T> T invokeRestCall(OAuth2ResourceIdentifier targetedService, String uriPath,
      Map<String, String> additionalHeader, HttpMethod method, Object jsonBody,
      Class<T> targetReturnType) {
    final String url = url(targetedService, uriPath);
    final HttpHeaders headers = headers(additionalHeader);
    final HttpEntity<?> httpEntity = new HttpEntity<Object>(jsonBody, headers);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return restTemplate.exchange(url, method, httpEntity, targetReturnType).getBody();
  }


  @Override
  @HystrixCommand()
  public <T> T invokeRestCall(OAuth2ResourceIdentifier targetedService, String uriPath,
      Map<String, String> additionalHeader, HttpMethod method, Map<String, String> parameters,
      Class<T> targetReturnType) {
    final String url = url(targetedService, uriPath);
    final HttpHeaders headers = headers(additionalHeader);
    final LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
    if (parameters != null) {
      parameters.entrySet().forEach(e -> body.add(e.getKey(), e.getValue()));
    }
    final HttpEntity<?> httpEntity = new HttpEntity<Object>(body, headers);
    return restTemplate.exchange(url, method, httpEntity, targetReturnType).getBody();
  }

}
