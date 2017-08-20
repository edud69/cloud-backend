

package io.theshire.authorization.server.configuration.oauth2;

import io.theshire.authorization.server.jwt.JwtAuthorizationServerAccessConverter;
import io.theshire.authorization.server.session.PersistentSessionStrategy;
import io.theshire.authorization.service.authentication.resquest.details.AuthenticationRequestDetailsFilter;
import io.theshire.authorization.service.authentication.resquest.details.AuthenticationRequestTenantFilter;
import io.theshire.authorization.service.user.details.UserDetailsAuthService;
import io.theshire.common.utils.oauth2.resource.identifier.OAuth2ResourceIdentifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;


@Configuration
@EnableAuthorizationServer
@Import(value = { OAuth2ProvidersConfigurer.class, OAuth2WebSecurity.class,
    OAuth2SocialSignupConfigurer.class })
public class OAuth2AuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

 
  @Value("${app.cloud.security.oauth.accessTokenValiditySeconds}")
  private Integer accessTokenValiditySeconds;

 
  @Value("${app.cloud.security.oauth.refreshTokenValiditySeconds}")
  private Integer refreshTokenValiditySeconds;

 
  @Value("${app.cloud.security.oauth.client.internal.id}")
  private String internalWebClientId;

 
  @Value("${app.cloud.security.oauth.client.internal.secret}")
  private String internalWebClientSecret;

 
  @Value("${app.cloud.security.oauth.client.public.web.id}")
  private String publicWebClientId;

 
  @Value("${app.cloud.security.oauth.client.public.web.secret}")
  private String publicWebClientSecret;

 
  @Value("${app.cloud.security.oauth.redirectSigninUrl}")
  private String oauthPublicWebClientRedirectUrl;

 
  @Value("${app.cloud.security.oauth.jwt.privateKey.password}")
  private String jwtPrivateKeyPassword;

 
  @Value("${app.cloud.security.oauth.jwt.privateKey.keystore.password}")
  private String jwtKeystorePass;

 
  @Value("${app.cloud.security.oauth.jwt.keystore.alias}")
  private String jwtKeystoreAlias;

 
  @Autowired
  private AuthenticationManager authenticationManager;

 
  @Autowired
  private UserDetailsAuthService userDetailsAuthService;

 
  @Autowired
  private PersistentSessionStrategy persistentSessionStrategy;

 
  @Bean
  protected JwtAccessTokenConverter accessTokenConverter(
      final UserDetailsAuthService userDetailsAuthService) throws NoSuchAlgorithmException {
    final JwtAuthorizationServerAccessConverter jwtAccessTokenConverter =
        new JwtAuthorizationServerAccessConverter(userDetailsAuthService);
    final KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("jwt-key.jks"),
        jwtPrivateKeyPassword.toCharArray()).getKeyPair(jwtKeystoreAlias,
            jwtKeystorePass.toCharArray());
    jwtAccessTokenConverter.setKeyPair(keyPair);
    return jwtAccessTokenConverter;
  }

 
  @Bean
  protected JwtTokenStore jwtTokenStore(UserDetailsAuthService userDetailsAuthService)
      throws Exception {
    return new JwtTokenStore(accessTokenConverter(userDetailsAuthService));
  }


  @Override
  public void configure(final AuthorizationServerSecurityConfigurer security) throws Exception {
    // gives access to the jwt public key for everyone
    final List<Filter> filters = new ArrayList<>(2);
    filters.add(new AuthenticationRequestDetailsFilter(persistentSessionStrategy));
    filters.add(new AuthenticationRequestTenantFilter());
    security.tokenKeyAccess("permitAll()").tokenEndpointAuthenticationFilters(filters);
  }

 

  @Override
  public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints.tokenStore(jwtTokenStore(userDetailsAuthService))
        .accessTokenConverter(accessTokenConverter(userDetailsAuthService))
        .tokenEnhancer(accessTokenConverter(userDetailsAuthService))
        .authenticationManager(authenticationManager).userDetailsService(userDetailsAuthService);
  }

 

  @Override
  public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
    // grantTypes : "authorization_code", "refresh_token", "implicit",
    // "password", "client_credentials"
    inMemoryWebClient(clients);
    // privateClients(clients);
  }

 
  private String[] enabledResourceIds() {
    final List<String> enabledResourceIds = new ArrayList<>();
    Arrays.asList(OAuth2ResourceIdentifier.values()).stream()
        .forEach(service -> enabledResourceIds.add(service.getResourceId()));
    return enabledResourceIds.toArray(new String[enabledResourceIds.size()]);
  }

 
  private String[] enabledWithoutAdminResourceIds() {
    final List<String> enabledResourceIds = new ArrayList<>();
    Arrays.asList(OAuth2ResourceIdentifier.values()).stream()
        .filter(elem -> !OAuth2ResourceIdentifier.enumerateAdminServices().contains(elem))
        .forEach(service -> enabledResourceIds.add(service.getResourceId()));
    return enabledResourceIds.toArray(new String[enabledResourceIds.size()]);
  }

 
  private void inMemoryWebClient(final ClientDetailsServiceConfigurer clients) throws Exception {
    // public web client
    final ClientDetailsServiceBuilder<InMemoryClientDetailsServiceBuilder> memClients =
        clients.inMemory().withClient(publicWebClientId).secret(publicWebClientSecret)
            .authorizedGrantTypes("refresh_token", "password").scopes("web_access")
            .redirectUris(oauthPublicWebClientRedirectUrl)
            .accessTokenValiditySeconds(accessTokenValiditySeconds)
            .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
            .resourceIds(enabledWithoutAdminResourceIds()).autoApprove(true).and();

    // internal client (Admin and system)
    memClients.withClient(internalWebClientId).secret(internalWebClientSecret)
        .authorizedGrantTypes("refresh_token", "password").scopes("admin_access")
        .accessTokenValiditySeconds(accessTokenValiditySeconds)
        .refreshTokenValiditySeconds(refreshTokenValiditySeconds).resourceIds(enabledResourceIds())
        .autoApprove(true);
  }

}
