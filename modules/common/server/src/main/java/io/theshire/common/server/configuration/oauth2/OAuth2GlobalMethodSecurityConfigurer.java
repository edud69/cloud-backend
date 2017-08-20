

package io.theshire.common.server.configuration.oauth2;

import io.theshire.common.server.configuration.permission.CustomPermissionEvaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Import(CustomPermissionEvaluator.class)
public class OAuth2GlobalMethodSecurityConfigurer extends GlobalMethodSecurityConfiguration {

 
  @Autowired
  private CustomPermissionEvaluator customPermissionEvaluator;


  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    final DefaultMethodSecurityExpressionHandler expressionHandler =
        new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(customPermissionEvaluator);
    return expressionHandler;
  }

}
