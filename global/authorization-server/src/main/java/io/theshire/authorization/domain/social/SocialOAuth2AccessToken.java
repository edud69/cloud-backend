

package io.theshire.authorization.domain.social;

import com.google.common.collect.Sets;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;


public class SocialOAuth2AccessToken implements OAuth2AccessToken {

 
  private static final Set<String> scopes =
      Collections.unmodifiableSet(Sets.newHashSet("social_operations"));

 
  private final Connection<?> connection;

 
  private final ConnectionData connectionData;

 
  private final OAuth2RefreshToken refreshToken;

 
  public SocialOAuth2AccessToken(final Connection<?> connection) {
    this.connection = connection;
    this.connectionData = connection.createData();
    this.refreshToken = () -> connectionData.getRefreshToken();
  }


  @Override
  public Map<String, Object> getAdditionalInformation() {
    return null;
  }


  @Override
  public Set<String> getScope() {
    return scopes;
  }


  @Override
  public OAuth2RefreshToken getRefreshToken() {
    return refreshToken;
  }


  @Override
  public String getTokenType() {
    return connectionData.getProviderId();
  }


  @Override
  public boolean isExpired() {
    return connection.hasExpired();
  }


  @Override
  public Date getExpiration() {
    return new Date(connectionData.getExpireTime());
  }


  @Override
  public int getExpiresIn() {
    final LocalDate tokenDate = LocalDate.ofEpochDay(connectionData.getExpireTime());
    final LocalDate now = LocalDate.now();
    final long seconds = now.until(tokenDate, ChronoUnit.SECONDS);
    final int secondsInt = Math.toIntExact(seconds);
    return secondsInt;
  }


  @Override
  public String getValue() {
    return connectionData.getAccessToken();
  }
}
