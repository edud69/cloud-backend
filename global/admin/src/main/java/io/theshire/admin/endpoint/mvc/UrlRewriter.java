

package io.theshire.admin.endpoint.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;


@Component
public class UrlRewriter {

 
  private static String HYPERLINK_PREFIX = "href=\"";

 
  private static String SRC_PREFIX = "src=\"";

 
  private static String HTTP_PROTOCOL = "http://";

 
  private static String HTTPS_PROTOCOL = "https://";

 
  private static final String JAVASCRIPT_LINK = "javascript://";

 
  private static Pattern pattern =
      Pattern.compile("(" + SRC_PREFIX + "|" + HYPERLINK_PREFIX + ")(.+?)\"");

 
  @Autowired
  private LoadBalancedRequestContextResolver loadBalancedRequestContextResolver;

 
  public String rewriteHyperlinks(final String htmlContent, final HttpServletRequest request) {
    final String requestContext =
        loadBalancedRequestContextResolver.getLoadBalancedContextUrl(request);
    final StringBuffer output = new StringBuffer();
    final Matcher matcher = pattern.matcher(htmlContent);

    while (matcher.find()) {
      final String currentLink = matcher.group();
      String newLink = currentLink.replace(HYPERLINK_PREFIX, "");
      newLink = newLink.replace(SRC_PREFIX, "");
      newLink = newLink.replace("\"", "");

      if (newLink.startsWith(HTTP_PROTOCOL) || newLink.startsWith(HTTPS_PROTOCOL)
          || newLink.equals(JAVASCRIPT_LINK)) {
        continue;
      }

      if (newLink.startsWith("/")) {
        newLink = requestContext + newLink.substring(1);
      } else {
        String uri = request.getRequestURI();
        uri = uri.substring(0, uri.lastIndexOf("/"));
        newLink = requestContext
            + (uri.length() == 1 ? "" : uri.length() > 0 ? uri.substring(1) + "/" : "") + newLink;
      }

      final String replacement = currentLink.replaceAll("\"(.+?)\"", "\"" + newLink + "\"");

      matcher.appendReplacement(output, replacement);
    }

    matcher.appendTail(output);

    return output.toString();
  }

}
