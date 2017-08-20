

package io.theshire.authorization.server.configuration.oauth2;

import io.theshire.authorization.domain.social.SocialProvider;
import io.theshire.authorization.server.session.PersistentSessionStrategy;
import io.theshire.authorization.service.social.oauth2.OAuth2ConnectionFactoryConfiguration;
import io.theshire.authorization.service.social.oauth2.OAuth2Providers;
import io.theshire.common.server.tenant.TenantDatabaseSchema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.sql.DataSource;


@Configuration
@EnableSocial
public class OAuth2SocialSignupConfigurer {

 
  private static class CustomConnectController extends ConnectController {

   
    private final String callbackUrl;

   
    public CustomConnectController(String callbackUrl,
        ConnectionFactoryLocator connectionFactoryLocator,
        ConnectionRepository connectionRepository) {
      super(connectionFactoryLocator, connectionRepository);
      this.callbackUrl = callbackUrl;
    }

  
    @Override
    protected RedirectView connectionStatusRedirect(String providerId, NativeWebRequest request) {
      if (request.getParameter("code") != null) {
        return new RedirectView(callbackUrl);
      }
      return super.connectionStatusRedirect(providerId, request);
    }
  }

 
  @Bean
  public ConnectController connectController(Environment env,
      ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository,
      PersistentSessionStrategy persistentSessionStrategy) {
    String callbackUrl = env.getProperty("app.cloud.security.oauth.redirectSigninUrl");
    callbackUrl = callbackUrl.endsWith("/") ? callbackUrl.substring(0, callbackUrl.length() - 1)
        : callbackUrl;
    ConnectController controller =
        new CustomConnectController(callbackUrl, connectionFactoryLocator, connectionRepository);
    String appUrl = env.getProperty("app.cloud.security.oauth.applicationUrl");
    appUrl = appUrl.endsWith("/") ? appUrl.substring(0, appUrl.length() - 1) : appUrl;
    controller.setApplicationUrl(appUrl);
    controller.setSessionStrategy(persistentSessionStrategy);
    return controller;
  }

 
  @Bean
  public ProviderSignInController providerSignInController(
      final ConnectionFactoryLocator connectionFactoryLocator,
      final UsersConnectionRepository usersConnectionRepository, final Environment env,
      final SignInAdapter socialSignInService,
      final PersistentSessionStrategy persistentSessionStrategy) {
    final ProviderSignInController controller = new ProviderSignInController(
        connectionFactoryLocator, usersConnectionRepository, socialSignInService);
    String baseUrl = env.getProperty("app.cloud.security.oauth.applicationUrl");
    baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    controller.setApplicationUrl(baseUrl);
    controller.setSignInUrl(env.getProperty("app.cloud.security.oauth.redirctSigninUrl"));
    controller.setSignUpUrl(env.getProperty("app.cloud.security.oauth.redirctSignupUrl"));
    controller.setSessionStrategy(persistentSessionStrategy);
    return controller;
  }

 
  @Bean
  @Autowired
  public FacebookConnectionFactory
      facebookConnectionFactory(final OAuth2Providers oauth2Provdiders) {
    OAuth2ConnectionFactoryConfiguration facebook = oauth2Provdiders.get(SocialProvider.FACEBOOK);
    FacebookConnectionFactory factory =
        new FacebookConnectionFactory(facebook.getAppId(), facebook.getAppSecret());
    factory.setScope(facebook.getScope());
    return factory;
  }

 
  @Bean
  @Autowired
  public GoogleConnectionFactory googleConnectionFactory(final OAuth2Providers oauth2Providers) {
    OAuth2ConnectionFactoryConfiguration google = oauth2Providers.get(SocialProvider.GOOGLE);
    GoogleConnectionFactory factory =
        new GoogleConnectionFactory(google.getAppId(), google.getAppSecret());
    factory.setScope(google.getScope());
    return factory;
  }

 
  @Bean
  @Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
  public ConnectionFactoryLocator connectionFactoryLocator(FacebookConnectionFactory facebook,
      GoogleConnectionFactory google) {
    final ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
    registry.addConnectionFactory(facebook);
    registry.addConnectionFactory(google);
    return registry;
  }

 
  @Bean
  @Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
  public UsersConnectionRepository usersConnectionRepository(final DataSource ds,
      final Environment env, final ConnectionSignUp signUpService,
      ConnectionFactoryLocator connectionFactoryLocator) {
    final JdbcUsersConnectionRepository repository =
        new JdbcUsersConnectionRepository(ds, connectionFactoryLocator,
            Encryptors.text(env.getProperty("app.cloud.security.oauth.encryptor.password"),
                KeyGenerators.string().generateKey()));
    repository.setConnectionSignUp(signUpService);
    repository.setTablePrefix(TenantDatabaseSchema.SINGLE_SIGN_ON_SCHEMA + ".");
    return repository;
  }

}
