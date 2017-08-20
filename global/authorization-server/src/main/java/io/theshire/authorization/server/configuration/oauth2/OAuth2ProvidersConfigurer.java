

package io.theshire.authorization.server.configuration.oauth2;

import io.theshire.authorization.domain.social.SocialProvider;
import io.theshire.authorization.service.social.oauth2.OAuth2ConnectionFactoryConfiguration;
import io.theshire.authorization.service.social.oauth2.OAuth2Providers;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;




@Slf4j
@Configuration
public class OAuth2ProvidersConfigurer {

 
  @Bean
  protected OAuth2Providers oauth2Providers(final Environment env) {
    log.info("Initializing OAuth2 Providers...");
    final Map<String, OAuth2ConnectionFactoryConfiguration> providerConfigs = new HashMap<>();
    registerProvider(SocialProvider.FACEBOOK, providerConfigs, env);
    registerProvider(SocialProvider.GOOGLE, providerConfigs, env);
    return new OAuth2ProvidersImpl(providerConfigs);
  }

 
  private void registerProvider(final SocialProvider socialProvider,
      final Map<String, OAuth2ConnectionFactoryConfiguration> registry, final Environment env) {
    final String appId = env.getProperty("app.cloud.social." + socialProvider.getId() + ".appId");
    Assert.notNull(appId, "appId cannot be null.");

    final String appSecret =
        env.getProperty("app.cloud.social." + socialProvider.getId() + ".appSecret");
    Assert.notNull(appSecret, "appSecret cannot be null.");

    final String scope = env.getProperty("app.cloud.social." + socialProvider.getId() + ".scope");
    Assert.notNull(scope, "scope cannot be null.");

    final String emailDomain =
        env.getProperty("app.cloud.social." + socialProvider.getId() + ".emailDomain");
    Assert.notNull(emailDomain, "emailDomain cannot be null.");

    final OAuth2ConnectionFactoryConfiguration connFactoryConfig =
        new OAuth2ConnectionFactoryConfiguration();
    connFactoryConfig.setAppId(appId);
    connFactoryConfig.setAppSecret(appSecret);
    connFactoryConfig.setScope(scope);
    connFactoryConfig.setEmailDomain(emailDomain);

    registry.put(socialProvider.getId(), connFactoryConfig);
  }

}
