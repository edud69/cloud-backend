

package io.theshire.common.utils.security.authentication;

import io.theshire.common.utils.security.constants.SecurityAuthenticationKeyDetailConstants;

import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;


public class AuthenticationDetails {

 
  private final Map<String, Object> details;

 
  public AuthenticationDetails(final Map<String, Object> details) {
    Assert.notNull(details, "details cannot be null.");
    this.details = details;
  }

 
  public String getTenantIdentifier() {
    return (String) details.get(SecurityAuthenticationKeyDetailConstants.TENANT_IDENTIFIER);
  }

 
  public Long getUserId() {
    return (Long) details.get(SecurityAuthenticationKeyDetailConstants.USER_ID);
  }

 
  public String getJwtToken() {
    return (String) details.get(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN);
  }

 
  public LocalDateTime getJwtTokenCreationTime() {
    return fromEpochSecondToLocalDateTimeAtUtc(
        (Long) details.get(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_CREATION_TIME));
  }

 
  public LocalDateTime getJwtTokenExpireTime() {
    return fromEpochSecondToLocalDateTimeAtUtc(
        (Long) details.get(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_EXPIRE_TIME));
  }

 
  private LocalDateTime fromEpochSecondToLocalDateTimeAtUtc(final Long epochSecond) {
    if (epochSecond == null) {
      return null;
    }

    return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochSecond * 1000),
        ZoneId.of(ZoneOffset.UTC.getId()));
  }

}
