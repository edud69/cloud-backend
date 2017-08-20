

package io.theshire.authorization.server.jwt;

import io.theshire.authorization.domain.jwt.JwtTokenRevokedException;
import io.theshire.authorization.domain.user.details.UserDetailsExtended;
import io.theshire.authorization.service.user.details.UserDetailsAuthService;
import io.theshire.common.server.jwt.JwtAccessConverter;
import io.theshire.common.server.jwt.JwtAdditionalClaim;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;


public class JwtAuthorizationServerAccessConverter extends JwtAccessConverter {

 
  private static final String USERNAME = "user_name";

 
  private final UserDetailsAuthService userDetailsAuthService;

 
  public JwtAuthorizationServerAccessConverter(
      final UserDetailsAuthService userDetailsAuthService) {
    super();
    this.userDetailsAuthService = userDetailsAuthService;
  }


  @Override
  public OAuth2AccessToken enhance(final OAuth2AccessToken accessToken,
      final OAuth2Authentication authentication) {
    final DefaultOAuth2AccessToken enhanced = new DefaultOAuth2AccessToken(accessToken);
    final Map<String, Object> info =
        new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
    enhanced.setAdditionalInformation(info);
    enhanceWithDetails(enhanced, authentication);
    return super.enhance(enhanced, authentication);
  }

 
  private void enhanceWithDetails(final OAuth2AccessToken enhanced,
      final OAuth2Authentication auth) {
    final UserDetailsExtended userDetailsExtended = (UserDetailsExtended) auth.getPrincipal();
    final String tid = userDetailsExtended.getTenantIdentifier();
    if (tid != null) {
      enhanced.getAdditionalInformation().put(JwtAdditionalClaim.TID.toString(), tid);
    }
    enhanced.getAdditionalInformation().put(JwtAdditionalClaim.CTM.toString(),
        Instant.now().toEpochMilli() / 1000);
    enhanced.getAdditionalInformation().put(JwtAdditionalClaim.UID.toString(),
        userDetailsExtended.getUserId());
  }


  @Override
  protected Map<String, Object> decode(String token) {
    final Map<String, Object> tokenInfo = super.decode(token);
    final boolean refreshToken = tokenInfo != null && tokenInfo.get(ATI) != null;

    if (refreshToken) {
      final String username = (String) tokenInfo.get(USERNAME);
      final UserDetailsExtended userDetailsExtended =
          (UserDetailsExtended) userDetailsAuthService.loadUserByUsername(username);
      final Object time = tokenInfo.get(JwtAdditionalClaim.CTM.toString());
      final Instant instant = Instant.ofEpochSecond((Long) time);
      final LocalDateTime tokenCreationTime =
          LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));

      if (userDetailsExtended.getPasswordUpdateTime() != null
          && userDetailsExtended.getPasswordUpdateTime().isAfter(tokenCreationTime)) {
        throw new RuntimeException("See wrapped exception for details.",
            new JwtTokenRevokedException("Token is not valid, a password has been changed "
                + "on the entity related to this token."));
      }
    }

    return tokenInfo;
  }

}
