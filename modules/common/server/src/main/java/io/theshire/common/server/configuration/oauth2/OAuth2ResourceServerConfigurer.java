

package io.theshire.common.server.configuration.oauth2;

import io.theshire.common.server.cors.CorsFilter;
import io.theshire.common.server.jwt.JwtResourceServerAccessConverter;
import io.theshire.common.server.tenant.TenantContextFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@EnableResourceServer
@Configuration
@Import(
    value = { JwtResourceServerAccessConverter.class, OAuth2GlobalMethodSecurityConfigurer.class })
public class OAuth2ResourceServerConfigurer extends ResourceServerConfigurerAdapter {

 
  @Autowired
  private JwtResourceServerAccessConverter jwtResourceServerAccessConverter;

 
  @Autowired
  private CorsFilter corsFilter;

 
  public static final Set<String> permitAllPaths = Collections.unmodifiableSet(
      new HashSet<>(Arrays.asList(new String[] { "/mappings", "/metrics", "/metrics/**",
          "/features", "/autoconfig", "/beans", "/info", "/health", "/env", "/env/**", "/trace",
          "/pause", "/resume", "/refresh", "/restart", "/archaius", "/configprops", "/dump" })));

 
  public static final Set<String> blockedAllPaths = Collections.unmodifiableSet(
      new HashSet<>(Arrays.asList(new String[] { "/hystrix.stream", "/hystrix.stream/**" })));

 
  @Bean
  protected JwtTokenStore jwtTokenStore() {
    return new JwtTokenStore(jwtResourceServerAccessConverter);
  }


  @Override
  public void configure(final ResourceServerSecurityConfigurer resources) throws Exception {
    resources.tokenStore(jwtTokenStore());
  }


  @Override
  public void configure(final HttpSecurity http) throws Exception {
    applyFilters(http);
    applyStatelessSessionPolicy(http);

    http.authorizeRequests().antMatchers(permitAllPaths.toArray(new String[permitAllPaths.size()]))
        .permitAll();

    http.authorizeRequests()
        .antMatchers(blockedAllPaths.toArray(new String[blockedAllPaths.size()])).denyAll();

    http.authorizeRequests().anyRequest().authenticated();
  }

 
  private void applyStatelessSessionPolicy(final HttpSecurity http) throws Exception {
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

 
  private void applyFilters(final HttpSecurity http) throws Exception {
    http.addFilterAfter(new TenantContextFilter(), AnonymousAuthenticationFilter.class)
        .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);
  }

}
