

package io.theshire.gateway.server.mvc.error;

import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlAllowHeaders;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlAllowMethods;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlAllowOrigin;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlMaxAge;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class GatewayErrorControllerTest {

 
  private GatewayErrorController classUnderTest;

 
  @Before
  public void setup() {
    this.classUnderTest = new GatewayErrorController();
  }

 
  @Test
  public void shouldGetErrorPath() {
    ReflectionTestUtils.setField(classUnderTest, "errorPath", "path/to/error");
    Assert.assertEquals("path/to/error", classUnderTest.getErrorPath());
  }

 
  @Test
  public void shouldRedirectToView() {
    final String allowedOrigin = "allowedOriginValue";
    ReflectionTestUtils.setField(classUnderTest, "allowedOrigin", allowedOrigin);
    final String allowedMethods = "allowedMethodsValue";
    ReflectionTestUtils.setField(classUnderTest, "allowedMethods", allowedMethods);
    final String allowedHeaders = "allowedHeadersValue";
    ReflectionTestUtils.setField(classUnderTest, "allowedHeaders", allowedHeaders);
    final String maxAge = "maxAgeValue";
    ReflectionTestUtils.setField(classUnderTest, "maxAge", maxAge);

    ReflectionTestUtils.setField(classUnderTest, "errorViewUrl",
        "http://somedomain.com/error/view/url");

    final HttpServletResponse mockedResponse = Mockito.mock(HttpServletResponse.class);
    Mockito.when(mockedResponse.getHeader(HTTP_HEADER_accessControlAllowOrigin)).thenReturn(null);

    final HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
    Mockito.when(mockedRequest.getAttribute("javax.servlet.error.status_code"))
        .thenReturn(HttpStatus.BAD_REQUEST.value());

    final ResponseEntity<String> responseEntity =
        new ResponseEntity<>("body", HttpStatus.BAD_REQUEST);
    final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    Mockito
        .when(restTemplate.getForEntity(
            "http://somedomain.com/error/view/url" + HttpStatus.BAD_REQUEST.value(), String.class))
        .thenReturn(responseEntity);
    ReflectionTestUtils.setField(classUnderTest, "restTemplate", restTemplate);

    final ResponseEntity<String> response =
        classUnderTest.redirectToView(mockedRequest, mockedResponse);

    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assert.assertEquals("body", response.getBody());
    Assert.assertEquals(allowedOrigin,
        response.getHeaders().get(HTTP_HEADER_accessControlAllowOrigin).get(0));
    Assert.assertEquals(allowedMethods,
        response.getHeaders().get(HTTP_HEADER_accessControlAllowMethods).get(0));
    Assert.assertEquals(allowedHeaders,
        response.getHeaders().get(HTTP_HEADER_accessControlAllowHeaders).get(0));
    Assert.assertEquals(maxAge, response.getHeaders().get(HTTP_HEADER_accessControlMaxAge).get(0));
  }

}
