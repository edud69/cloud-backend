

package io.theshire.authorization.server.configuration.authentication;

import io.theshire.authorization.service.user.details.UserDetailsAuthService;
import io.theshire.common.utils.security.encryptor.CredentialsEncryptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;


@Configuration
public class AuthenticationManagerConfigurer extends GlobalAuthenticationConfigurerAdapter {

 
  @Autowired
  private UserDetailsAuthService userDetailsAuthService;


  @Override
  public void init(final AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsAuthService).passwordEncoder(new CredentialsEncryptor());
  }

}
