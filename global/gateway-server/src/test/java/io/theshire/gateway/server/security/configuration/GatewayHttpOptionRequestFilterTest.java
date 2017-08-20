

package io.theshire.gateway.server.security.configuration;

import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlAllowHeaders;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlAllowMethods;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlAllowOrigin;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_accessControlMaxAge;

import io.theshire.common.utils.http.constants.HttpHeaderConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RunWith(PowerMockRunner.class)
public class GatewayHttpOptionRequestFilterTest {

 
  @Mock
  private HttpServletRequest request;

 
  @Mock
  private HttpServletResponse response;

 
  @Mock
  private FilterChain filterChain;

 
  private GatewayHttpOptionRequestFilter classUnderTest;

 
  @Before
  public void setup() {
    this.classUnderTest = new GatewayHttpOptionRequestFilter();
  }

 
  @Test
  public void shouldNotAddHeadersWhenNotOptions() throws ServletException, IOException {
    Mockito.when(request.getMethod()).thenReturn(RequestMethod.GET.name());
    this.classUnderTest.doFilterInternal(request, response, filterChain);
    Assert.assertTrue(response.getHeaderNames().isEmpty());
    Mockito.verify(response, Mockito.never())
        .getHeader(HttpHeaderConstants.HTTP_HEADER_accessControlAllowOrigin);
    Mockito.verify(filterChain).doFilter(request, response);
  }

 
  @Test
  public void shouldAddHeadersWhenOptions() throws IOException, ServletException {
    final String allowedOrigin = "allowedOriginValue";
    ReflectionTestUtils.setField(classUnderTest, "allowedOrigin", allowedOrigin);
    final String allowedMethods = "allowedMethodsValue";
    ReflectionTestUtils.setField(classUnderTest, "allowedMethods", allowedMethods);
    final String allowedHeaders = "allowedHeadersValue";
    ReflectionTestUtils.setField(classUnderTest, "allowedHeaders", allowedHeaders);
    final String maxAge = "maxAgeValue";
    ReflectionTestUtils.setField(classUnderTest, "maxAge", maxAge);

    Mockito.when(request.getMethod()).thenReturn(RequestMethod.OPTIONS.name());

    final PrintWriter mockedWriter = Mockito.mock(PrintWriter.class);
    Mockito.when(response.getWriter()).thenReturn(mockedWriter);

    this.classUnderTest.doFilterInternal(request, response, filterChain);

    Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    Mockito.verify(response).setHeader(HTTP_HEADER_accessControlAllowOrigin, allowedOrigin);
    Mockito.verify(response).setHeader(HTTP_HEADER_accessControlAllowMethods, allowedMethods);
    Mockito.verify(response).setHeader(HTTP_HEADER_accessControlAllowHeaders, allowedHeaders);
    Mockito.verify(response).setHeader(HTTP_HEADER_accessControlMaxAge, maxAge);
    Mockito.verify(mockedWriter).print(HttpStatus.OK);
    Mockito.verify(mockedWriter).flush();
  }

}
