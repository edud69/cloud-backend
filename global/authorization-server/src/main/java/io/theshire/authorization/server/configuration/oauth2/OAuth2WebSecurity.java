

package io.theshire.authorization.server.configuration.oauth2;

import io.theshire.authorization.server.session.PersistentSessionStrategy;
import io.theshire.authorization.service.authentication.resquest.details.AuthenticationRequestDetailsFilter;
import io.theshire.authorization.service.authentication.resquest.details.AuthenticationRequestTenantFilter;
import io.theshire.common.server.configuration.oauth2.OAuth2ResourceServerConfigurer;
import io.theshire.common.server.cors.CorsFilter;
import io.theshire.common.utils.authentication.OAuth2AuthenticationManagerBuilder;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;


@EnableWebSecurity
@Import(OAuth2WebMvcCorsMapper.class)
public class OAuth2WebSecurity extends WebSecurityConfigurerAdapter {

 
  @Autowired
  private CorsFilter corsFilter;

 
  @Autowired
  private PersistentSessionStrategy persistentSessionStrategy;

 
  private String[] permitAllPaths = new String[] { "/signin/**", "/signup/**", "/user/subscription",
      "/user/subscription/activation", "/password/lost", "/password/restore", "/token/refresh",
      "/admin/token/refresh" };

 
  @Autowired
  private JwtAccessTokenConverter jwtAccessTokenConverter;


  @Override
  public void configure(final HttpSecurity security) throws Exception {
    security.httpBasic().and().csrf().disable()
        .addFilterBefore(new AuthenticationRequestDetailsFilter(persistentSessionStrategy),
            AbstractPreAuthenticatedProcessingFilter.class)
        .addFilterAfter(new AuthenticationRequestTenantFilter(),
            AnonymousAuthenticationFilter.class)
        .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(oauth2AuthenticationProcessingFilter(),
            UsernamePasswordAuthenticationFilter.class)
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .authorizeRequests().antMatchers(permitAllPaths).permitAll().and().authorizeRequests()
        .antMatchers(OAuth2ResourceServerConfigurer.permitAllPaths
            .toArray(new String[OAuth2ResourceServerConfigurer.permitAllPaths.size()]))
        .permitAll().and().authorizeRequests()
        .antMatchers(OAuth2ResourceServerConfigurer.blockedAllPaths
            .toArray(new String[OAuth2ResourceServerConfigurer.blockedAllPaths.size()]))
        .denyAll().and().authorizeRequests().anyRequest().authenticated();
  }

 
  protected OAuth2AuthenticationProcessingFilter oauth2AuthenticationProcessingFilter() {
    final OAuth2AuthenticationManager oauth2AuthManager =
        new OAuth2AuthenticationManagerBuilder().resource(OAuth2ResourceIdentifier.AuthService)
            .jwtTokenEnhancer(jwtAccessTokenConverter).build();

    final OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter =
        new OAuth2AuthenticationProcessingFilter();
    oAuth2AuthenticationProcessingFilter.setAuthenticationManager(oauth2AuthManager);
    return oAuth2AuthenticationProcessingFilter;
  }

}
