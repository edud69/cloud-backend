

package io.theshire.authorization.service.impl.social;

import io.theshire.authorization.domain.user.details.UserDetailsExtended;
import io.theshire.authorization.service.user.details.UserDetailsAuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.Serializable;
import java.util.HashMap;


@Service
@Transactional
public class SocialSignInService implements SignInAdapter {

 
  @Autowired
  private UserDetailsAuthService userDetailsAuthService;

 
  @Autowired
  private ClientDetailsService clientDetailsService;

 
  @Autowired
  private AuthorizationServerTokenServices authorizationServerTokenServices;

 
  @Value("${app.cloud.security.oauth.redirectSigninUrl}")
  private String redirectUrl;

 
  @Value("${app.cloud.security.oauth.client.public.web.id}")
  private String publicClientId;


  @Override
  public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
    final UserDetailsExtended user =
        (UserDetailsExtended) userDetailsAuthService.loadUserByUsername(userId);
    final UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    final OAuth2AccessToken token = generateToken(auth);
    final StringBuilder sb = new StringBuilder(this.redirectUrl);
    final String redirect =
        sb.append("?refresh_token=").append(token.getRefreshToken().getValue()).toString();

    return redirect;
  }

 
  private OAuth2AccessToken generateToken(final Authentication userAuthentication) {
    final ClientDetails cl = clientDetailsService.loadClientByClientId(publicClientId);
    final OAuth2Request storedRequest = new OAuth2Request(new HashMap<String, String>(),
        cl.getClientId(), cl.getAuthorities(), cl.isScoped(), cl.getScope(), cl.getResourceIds(),
        "", cl.getAuthorizedGrantTypes(), new HashMap<String, Serializable>());
    final OAuth2Authentication oauth2Authentication =
        new OAuth2Authentication(storedRequest, userAuthentication);
    return authorizationServerTokenServices.createAccessToken(oauth2Authentication);
  }

}
