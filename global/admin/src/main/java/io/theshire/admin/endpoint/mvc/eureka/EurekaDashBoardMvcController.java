

package io.theshire.admin.endpoint.mvc.eureka;

import io.theshire.admin.endpoint.mvc.UrlRewriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;


@Controller
public class EurekaDashBoardMvcController {

 
  @Value("${eureka.client.serviceUrl.defaultZone}")
  private String registryServerUrl;

 
  private RestTemplate restTemplate = new RestTemplate();

 
  @Autowired
  private UrlRewriter urlRewriter;

 
  private String getRootUrl() {
    return this.registryServerUrl.replace("eureka/", "");
  }

 
  @ResponseBody
  @RequestMapping(value = "/eureka", method = RequestMethod.GET)
  public String eurekaRoot(final HttpServletRequest request) {
    final String content = restTemplate.getForEntity(getRootUrl(), String.class).getBody();
    return urlRewriter.rewriteHyperlinks(content, request);
  }

 
  @ResponseBody
  @RequestMapping(value = "/lastn", method = RequestMethod.GET)
  public String eurekaLastn(final HttpServletRequest request) {
    final String content =
        restTemplate.getForEntity(getRootUrl() + "lastn", String.class).getBody();
    return urlRewriter.rewriteHyperlinks(content, request);
  }

 
  @ResponseBody
  @RequestMapping(value = "/eureka/css/**", method = RequestMethod.GET)
  public String eurekaCss(final HttpServletRequest request) {
    final String path = getPath(request);
    return restTemplate.getForEntity(getRootUrl() + path, String.class).getBody();
  }

 
  @ResponseBody
  @RequestMapping(value = "/eureka/js/**", method = RequestMethod.GET)
  public String eurekaJs(final HttpServletRequest request) {
    final String path = getPath(request);
    return restTemplate.getForEntity(getRootUrl() + path, String.class).getBody();
  }

 
  @ResponseBody
  @RequestMapping(value = "/eureka/fonts/**", method = RequestMethod.GET)
  public byte[] eurekaFonts(final HttpServletRequest request) {
    final String path = getPath(request);
    return restTemplate.getForEntity(getRootUrl() + path, byte[].class).getBody();
  }

 
  @ResponseBody
  @RequestMapping(value = "/eureka/images/**", method = RequestMethod.GET)
  public byte[] eurekaImages(final HttpServletRequest request) {
    final String path = getPath(request);
    return restTemplate.getForEntity(getRootUrl() + path, byte[].class).getBody();
  }

 
  private String getPath(final HttpServletRequest request) {
    String uri = request.getRequestURI();
    final String[] fragments = uri.split("/eureka/");
    if (fragments.length == 2) {
      uri = fragments[1];
      uri = "eureka/" + uri;
    } else {
      uri = "";
    }

    return uri;
  }

}
