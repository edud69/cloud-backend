

package io.theshire.gateway.server.mvc.error;

import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlAllowHeaders;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlAllowMethods;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlAllowOrigin;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlMaxAge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
class GatewayErrorController implements ErrorController {

 
  @Value("${app.cloud.error.handling.view.url}")
  private String errorViewUrl;

 
  @Value("${error.path:/error}")
  private String errorPath;

 
  @Value("${app.cloud.security.cors.allowedOrigin}")
  private String allowedOrigin;

 
  @Value("${app.cloud.security.cors.allowedMethods}")
  private String allowedMethods;

 
  @Value("${app.cloud.security.cors.allowedHeaders}")
  private String allowedHeaders;

 
  @Value("${app.cloud.security.cors.maxAge}")
  private String maxAge;

 
  private final RestTemplate restTemplate = new RestTemplate();


  @Override
  public String getErrorPath() {
    return errorPath;
  }

 
  @RequestMapping(value = "${error.path:/error}")
  @ResponseBody
  public ResponseEntity<String> redirectToView(final HttpServletRequest request,
      final HttpServletResponse response) {
    final int status = getErrorStatus(request);
    final HttpStatus httpStatus = HttpStatus.valueOf(status);
    final MultiValueMap<String, String> customHeaders = createCorsHeadersIfNeeded(response);

    try {
      final String frontEndErrorPage = errorViewUrl + status;
      final String body = restTemplate.getForEntity(frontEndErrorPage, String.class).getBody();
      return new ResponseEntity<>(body, customHeaders, httpStatus);
    } catch (final Exception exc) {
      return new ResponseEntity<>("Unexpected Exception.", customHeaders, httpStatus);
    }
  }

 
  private MultiValueMap<String, String>
      createCorsHeadersIfNeeded(final HttpServletResponse response) {
    final HttpHeaders corsHeaders = new HttpHeaders();
    if (response.getHeader(HTTP_HEADER_accessControlAllowOrigin) == null) {
      corsHeaders.add(HTTP_HEADER_accessControlAllowOrigin, allowedOrigin);
      corsHeaders.add(HTTP_HEADER_accessControlAllowMethods, allowedMethods);
      corsHeaders.add(HTTP_HEADER_accessControlAllowHeaders, allowedHeaders);
      corsHeaders.add(HTTP_HEADER_accessControlMaxAge, maxAge);
    }

    return corsHeaders;
  }

 
  private int getErrorStatus(HttpServletRequest request) {
    final Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    return statusCode != null ? statusCode : HttpStatus.INTERNAL_SERVER_ERROR.value();
  }

}
