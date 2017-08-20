

package io.theshire.gateway.server.security.configuration;

import io.theshire.common.server.configuration.oauth2.OAuth2ResourceServerConfigurer;
import io.theshire.common.utils.security.role.constants.SecurityRoleConstants;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.HashSet;
import java.util.Set;


@Configuration
@EnableWebSecurity
@Import(GatewayHttpOptionRequestFilter.class)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {


  @Override
  public void configure(final HttpSecurity security) throws Exception {
    final Set<String> allowedManagementPaths = OAuth2ResourceServerConfigurer.permitAllPaths;
    final Set<String> blockedAsExternalPaths = new HashSet<String>();

    allowedManagementPaths.forEach(path -> {
      if (path.startsWith("")) {
        blockedAsExternalPaths.add(path);
      } else {
        blockedAsExternalPaths.add(path);
        blockedAsExternalPaths.add("/**" + path);
      }
    });

    security.authorizeRequests()
        .antMatchers(allowedManagementPaths.toArray(new String[allowedManagementPaths.size()]))
        .hasAnyAuthority(SecurityRoleConstants.ADMIN).and().authorizeRequests()
        .antMatchers(OAuth2ResourceServerConfigurer.blockedAllPaths
            .toArray(new String[OAuth2ResourceServerConfigurer.blockedAllPaths.size()]))
        .denyAll().and().authorizeRequests().antMatchers("/**").permitAll().and().headers()
        .disable().csrf().disable();
  }

}
