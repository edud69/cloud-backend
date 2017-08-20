

package io.theshire.common.server.jwt;

import io.theshire.common.utils.security.constants.SecurityAuthenticationKeyDetailConstants;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;


public abstract class JwtAccessConverter extends JwtAccessTokenConverter {


  @Override
  protected Map<String, Object> decode(String token) {
    final Map<String, Object> tokenInfo = super.decode(token);
    if (tokenInfo != null) {
      final Object creationTm = tokenInfo.get(JwtAdditionalClaim.CTM.toString());
      if (creationTm != null && creationTm instanceof Integer) {
        tokenInfo.put(JwtAdditionalClaim.CTM.toString(), ((Integer) creationTm).longValue());
      }

      final Object uid = tokenInfo.get(JwtAdditionalClaim.UID.toString());
      if (uid != null && uid instanceof Integer) {
        tokenInfo.put(JwtAdditionalClaim.UID.toString(), ((Integer) uid).longValue());
      }
    }

    return tokenInfo;
  }


  @Override
  public OAuth2AccessToken extractAccessToken(final String value, final Map<String, ?> map) {
    final OAuth2AccessToken oauth2Token = super.extractAccessToken(value, map);
    return oauth2Token;
  }


  @Override
  public OAuth2Authentication extractAuthentication(final Map<String, ?> map) {
    final OAuth2Authentication oAuth2Authentication = super.extractAuthentication(map);
    if (oAuth2Authentication.getUserAuthentication().getDetails() == null) {
      ((UsernamePasswordAuthenticationToken) oAuth2Authentication.getUserAuthentication())
          .setDetails(new HashMap<String, Object>());
    }

    addDetails(oAuth2Authentication.getUserAuthentication().getDetails(), map);
    return oAuth2Authentication;
  }

 
  private void addDetails(final Object details, final Map<String, ?> map) {
    @SuppressWarnings("unchecked")
    final Map<String, Object> detailsMap = (Map<String, Object>) details;
    map.entrySet().stream().filter(e -> e.getKey().equals(JwtAdditionalClaim.TID.toString()))
        .findFirst().ifPresent(e -> detailsMap
            .put(SecurityAuthenticationKeyDetailConstants.TENANT_IDENTIFIER, e.getValue()));
    map.entrySet().stream().filter(e -> e.getKey().equals(JwtAdditionalClaim.UID.toString()))
        .findFirst().ifPresent(e -> detailsMap.put(SecurityAuthenticationKeyDetailConstants.USER_ID,
            asLong(e.getValue())));
    map.entrySet().stream().filter(e -> e.getKey().equals(JwtAdditionalClaim.CTM.toString()))
        .findFirst().ifPresent(e -> {
          detailsMap.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_CREATION_TIME,
              e.getValue());
        });
    map.entrySet().stream().filter(e -> e.getKey().equals(EXP)).findFirst()
        .ifPresent(e -> detailsMap
            .put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_EXPIRE_TIME, e.getValue()));
  }

 
  private Long asLong(final Object input) {
    if (input != null) {
      if (input instanceof Integer) {
        return ((Integer) input).longValue();
      } else {
        return (Long) input;
      }
    }
    return null;
  }
}
