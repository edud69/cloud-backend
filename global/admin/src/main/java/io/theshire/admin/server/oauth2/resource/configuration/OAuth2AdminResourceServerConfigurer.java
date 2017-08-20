

package io.theshire.admin.server.oauth2.resource.configuration;

import io.theshire.admin.server.filter.AdminTenantContextFilter;
import io.theshire.admin.server.filter.SessionAuthorizationTokenFilter;
import io.theshire.admin.service.authentication.AdminPanelAuthenticationService;
import io.theshire.common.server.configuration.oauth2.OAuth2ResourceServerConfigurer;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;
import io.theshire.common.utils.security.constants.SecurityConstants;
import io.theshire.common.utils.security.role.constants.SecurityRoleConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;


public class OAuth2AdminResourceServerConfigurer extends OAuth2ResourceServerConfigurer {

 
  @Autowired
  private AdminPanelAuthenticationService adminPanelAuthenticationService;


  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    super.configure(resources);
    resources.resourceId(OAuth2ResourceIdentifier.AdminPanel.getResourceId());
  }


  @Override
  public void configure(final HttpSecurity http) throws Exception {
    http.addFilterAfter(new SessionAuthorizationTokenFilter(adminPanelAuthenticationService),
        AnonymousAuthenticationFilter.class)
        .addFilterAfter(new AdminTenantContextFilter(), AnonymousAuthenticationFilter.class);

    // Admin panel is stateful
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

    final String adminRole = SecurityRoleConstants.ADMIN.replace(SecurityConstants.ROLE_PREFIX, "");
    http.authorizeRequests().antMatchers("/", "/signin", "/info", "/health", "/hystrix-stream/**")
        .permitAll().anyRequest().hasRole(adminRole).anyRequest().authenticated();
  }
}
