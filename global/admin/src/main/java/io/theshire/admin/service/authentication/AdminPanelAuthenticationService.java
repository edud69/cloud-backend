

package io.theshire.admin.service.authentication;

import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;


public interface AdminPanelAuthenticationService {

 
  OAuth2Authentication authenticate(final String username, final String password);

 
  OAuth2Authentication refreshAuthentication(final OAuth2RefreshToken refreshToken);
}
