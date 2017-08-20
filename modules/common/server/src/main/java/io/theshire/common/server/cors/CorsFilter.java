

package io.theshire.common.server.cors;

import io.theshire.common.utils.http.constants.HttpHeaderConstants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class CorsFilter extends OncePerRequestFilter {

 
  @Value("${app.cloud.security.cors.allowedOrigin}")
  private String allowedOrigin;

 
  @Value("${app.cloud.security.cors.allowedMethods}")
  private String allowedMethods;

 
  @Value("${app.cloud.security.cors.allowedHeaders}")
  private String allowedHeaders;

 
  @Value("${app.cloud.security.cors.maxAge}")
  private String maxAge;

 
  public CorsFilter() {
    super();
  }


  @Override
  protected void doFilterInternal(final HttpServletRequest request,
      final HttpServletResponse response, final FilterChain filterChain)
      throws ServletException, IOException {
    if (response.getHeader(HttpHeaderConstants.HTTP_HEADER_accessControlAllowOrigin) == null) {
      response.setHeader(HttpHeaderConstants.HTTP_HEADER_accessControlAllowOrigin, allowedOrigin);
      response.setHeader(HttpHeaderConstants.HTTP_HEADER_accessControlAllowMethods, allowedMethods);
      response.setHeader(HttpHeaderConstants.HTTP_HEADER_accessControlAllowHeaders, allowedHeaders);
      response.setHeader(HttpHeaderConstants.HTTP_HEADER_accessControlMaxAge, maxAge);
    }

    if (RequestMethod.OPTIONS.name().equals(request.getMethod())) {
      response.getWriter().print(HttpStatus.OK);
      response.getWriter().flush();
    } else {
      filterChain.doFilter(request, response);
    }
  }

}
