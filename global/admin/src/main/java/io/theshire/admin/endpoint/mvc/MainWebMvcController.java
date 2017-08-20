

package io.theshire.admin.endpoint.mvc;

import io.theshire.admin.service.authentication.AdminPanelAuthenticationService;
import io.theshire.common.utils.security.authentication.AuthenticationContext;
import io.theshire.common.utils.security.authentication.AuthenticationInfo;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/")
@Slf4j
public class MainWebMvcController {

 
  private static final String OAUTH2AUTH_SESSION_PARAM_NAME = "oauth2_auth";

 
  @Autowired
  private AdminPanelAuthenticationService adminPanelAuthenticationService;

 
  @Autowired
  private LoadBalancedRequestContextResolver loadBalancedRequestContextResolver;

 
  @RequestMapping(value = { "", "/login" }, method = RequestMethod.GET)
  public ModelAndView index(final HttpServletRequest request) {
    return checkAndHandleLoggedUserIfExists("/login", request);
  }

 
  @RequestMapping(value = "/panel", method = RequestMethod.GET)
  public ModelAndView panel(final HttpServletRequest request) {
    return new ModelAndView("/panel", "requestContextPath", getLoadBalancedContextUrl(request));
  }

 
  private ModelAndView checkAndHandleLoggedUserIfExists(final String toView,
      final HttpServletRequest request) {
    final AuthenticationInfo auth = AuthenticationContext.get();
    if (auth != null && !auth.isAnonymousAuthentication()) {
      return new ModelAndView("/panel", "requestContextPath", getLoadBalancedContextUrl(request));
    }

    return new ModelAndView(toView, "requestContextPath", getLoadBalancedContextUrl(request));
  }

 
  private String getLoadBalancedContextUrl(final HttpServletRequest request) {
    return loadBalancedRequestContextResolver.getLoadBalancedContextUrl(request);
  }

 
  @RequestMapping(value = "/signout", method = RequestMethod.POST)
  public ModelAndView logout(final HttpServletRequest request, final HttpSession session) {
    try {
      request.logout();
    } catch (ServletException exc) {
      log.warn(String.format("Failed to logout httpServletRequest, request: %s.", request), exc);
    } finally {
      SecurityContextHolder.clearContext();
    }

    return index(request);
  }

 
  @RequestMapping(value = "/signin", method = RequestMethod.POST)
  public ModelAndView login(@RequestParam("username") final String username,
      @RequestParam("password") final String password, final HttpSession session,
      final HttpServletRequest request) {

    final Authentication authentication =
        adminPanelAuthenticationService.authenticate(username, password);
    if (authentication == null) {
      final ModelAndView mav = new ModelAndView("/login", "badCreds", true);
      mav.addObject("requestContextPath", getLoadBalancedContextUrl(request));
      return mav;
    }

    // stores the authentication in the session so next time token is retrieve from the session
    // directly
    session.setAttribute(OAUTH2AUTH_SESSION_PARAM_NAME, authentication);

    return new ModelAndView("/panel", "requestContextPath", getLoadBalancedContextUrl(request));
  }
}
