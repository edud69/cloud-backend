

package io.theshire.admin.endpoint.mvc.hystrix;

import io.theshire.admin.endpoint.mvc.LoadBalancedRequestContextResolver;
import io.theshire.admin.endpoint.mvc.UrlRewriter;
import io.theshire.common.service.infrastructure.bridge.MicroserviceBridgeService;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;
import io.theshire.common.utils.security.authentication.AuthenticationContext;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;




@Slf4j
@Controller
public class HystrixDashBoardMvcController {

 
  @Autowired
  private MicroserviceBridgeService microserviceBridgeService;

 
  @Autowired
  private UrlRewriter urlRewriter;

 
  @Autowired
  private LoadBalancedRequestContextResolver loadBalancedRequestContextResolver;

 
  @Autowired
  private LoadBalancerClient loadBalancerClient;

 
  @Autowired
  private JwtTokenStore jwtTokenStore;

 
  @ResponseBody
  @RequestMapping(value = "/hystrix-dashboard", method = RequestMethod.GET)
  public String hystrix(final HttpServletRequest request, final HttpSession session) {

    final Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + AuthenticationContext.get().getJwtToken());

    final String content = microserviceBridgeService.invokeRestCall(
        OAuth2ResourceIdentifier.AdminPanel, "/hystrix", headers, HttpMethod.GET, String.class);
    final String rewrittenContent = urlRewriter.rewriteHyperlinks(content, request);
    final String monitoringUrl = "/hystrix/monitor?stream=";

    return rewrittenContent.replace(monitoringUrl,
        loadBalancedRequestContextResolver.getLoadBalancedContextUrl(request)
            + "hystrix-dashboard/monitor?stream=");
  }

 
  @RequestMapping(value = "/hystrix-dashboard-with-stream", method = RequestMethod.GET)
  public String hystrixWithStream(final HttpServletRequest request, final HttpSession session)
      throws Exception {
    final String streamUrl = loadBalancedRequestContextResolver.getLoadBalancedContextUrl(request)
        + "hystrix-stream/" + AuthenticationContext.get().getJwtToken();
    final String encodedStreamUrl = URLEncoder.encode(streamUrl, "UTF-8");
    final String streamArgs =
        encodedStreamUrl + "&title=" + URLEncoder.encode("The Shire Cloud Services", "UTF-8");
    return "redirect:" + loadBalancedRequestContextResolver.getLoadBalancedContextUrl(request)
        + "hystrix-dashboard/monitor?stream=" + streamArgs;
  }

 
  @RequestMapping(value = "/hystrix-stream/**", method = RequestMethod.GET)
  public void hystrixStream(final HttpServletRequest request, final HttpServletResponse response,
      final HttpSession session) throws Exception {
    // make sure request is authenticated, this route is not secured
    final String queryStr = request.getRequestURL().toString();
    final int beginIdx = queryStr.indexOf("/hystrix-stream/");
    final int endIdx = queryStr.length();
    String token = queryStr.substring(beginIdx, endIdx);
    token = token.replace("/hystrix-stream/", "");
    final OAuth2AccessToken accessToken = jwtTokenStore.readAccessToken(token);
    log.debug("Access token found, {}.", accessToken);

    final ServiceInstance instance =
        loadBalancerClient.choose(OAuth2ResourceIdentifier.TurbineService.getMicroserviceName());
    final URL url = new URL("http://" + instance.getHost() + ":8989/");
    // Turbine AMQP port, not tomcat port

    final URLConnection connection = url.openConnection();
    response.setContentType(connection.getContentType());

    try (final InputStream is = connection.getInputStream()) {
      try (OutputStream os = response.getOutputStream()) {
        int bytesRead = -1;
        byte[] buffer = new byte[4096];
        while ((bytesRead = is.read(buffer)) != -1) {
          os.write(buffer, 0, bytesRead);
          os.flush();
        }
      }
    }
  }

 
  @ResponseBody
  @RequestMapping(value = "/hystrix-dashboard/{path}")
  public String hystrixMonitor(final HttpServletRequest request, final HttpSession session,
      @PathVariable String path) {

    final Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + AuthenticationContext.get().getJwtToken());

    final String content =
        microserviceBridgeService.invokeRestCall(OAuth2ResourceIdentifier.AdminPanel,
            "/hystrix/" + path, headers, HttpMethod.GET, String.class);
    return urlRewriter.rewriteHyperlinks(content, request).replace("/proxy.stream?origin=",
        loadBalancedRequestContextResolver.getLoadBalancedContextUrl(request)
            + "proxy.stream?origin=");
  }

 
  @ResponseBody
  @RequestMapping(value = { "/hystrix-dashboard/css/**", "/hystrix-dashboard/js/**",
      "/hystrix-dashboard/components/**" }, method = RequestMethod.GET)
  public String hystrixCssAndJs(final HttpServletRequest request) {
    final String path = getPath(request);
    final Map<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + AuthenticationContext.get().getJwtToken());

    return microserviceBridgeService.invokeRestCall(OAuth2ResourceIdentifier.AdminPanel,
        "/hystrix/" + path, headers, HttpMethod.GET, String.class);
  }

 
  private String getPath(final HttpServletRequest request) {
    String uri = request.getRequestURI();
    final String[] fragments = uri.split("/hystrix-dashboard/");
    if (fragments.length == 2) {
      uri = fragments[1];
    } else {
      uri = "";
    }

    return uri;
  }

}
