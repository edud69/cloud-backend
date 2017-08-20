

package io.theshire.admin.endpoint.mvc;

import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_forwardeHost;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_forwardedPort;
import static io.theshire.common.utils.http.constants.HttpHeaderConstants.HTTP_HEADER_forwardedPrefix;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Component
public class LoadBalancedRequestContextResolver {

 
  private static final String PROD_PROFILE = "prod";

 
  @Value("${spring.profiles.active}")
  private String activeProfiles;

 
  public String getLoadBalancedContextUrl(final HttpServletRequest request) {
    final StringBuilder sb = new StringBuilder();
    final String loadBalancedHost = request.getHeader(HTTP_HEADER_forwardeHost);
    final String loadBalancedPort = request.getHeader(HTTP_HEADER_forwardedPort);
    final String loadBalancedPrefix = request.getHeader(HTTP_HEADER_forwardedPrefix);

    if (loadBalancedHost != null) {
      boolean isProd = false;
      final String[] frags = activeProfiles.split(",");
      for (final String frag : frags) {
        if (frag.trim().equals(PROD_PROFILE)) {
          isProd = true;
          break;
        }
      }

      sb.append(isProd ? "https://" : "http://");
      if (loadBalancedPort != null) {
        String host = loadBalancedHost.split(":")[0];
        sb.append(host);
        sb.append(":");
        sb.append(loadBalancedPort);
      } else {
        sb.append(loadBalancedHost);
      }
      sb.append(loadBalancedPrefix.endsWith("/") ? loadBalancedPrefix : loadBalancedPrefix + "/");
    } else {
      sb.append(request.getScheme());
      sb.append("://");
      sb.append(request.getServerName());
      sb.append(":");
      sb.append(request.getServerPort());
      sb.append("/");
    }

    return sb.toString();
  }

}
