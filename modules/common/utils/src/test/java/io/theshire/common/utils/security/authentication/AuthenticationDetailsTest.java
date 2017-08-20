

package io.theshire.common.utils.security.authentication;

import io.theshire.common.utils.security.constants.SecurityAuthenticationKeyDetailConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;


public class AuthenticationDetailsTest {

 
  private AuthenticationDetails classUnderTest;

 
  private Map<String, Object> details = new HashMap<>();

 
  @Before
  public void setup() {
    this.classUnderTest = new AuthenticationDetails(details);
  }

 
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowOnNullCtorArg() {
    Assert.fail("Should fail if built : " + new AuthenticationDetails(null));
  }

 
  @Test
  public void shouldGetTenantIdentifier() {
    final String aTenant = "aTenant";
    this.details.put(SecurityAuthenticationKeyDetailConstants.TENANT_IDENTIFIER, aTenant);
    Assert.assertEquals(aTenant, this.classUnderTest.getTenantIdentifier());
  }

 
  @Test
  public void shouldGetUserId() {
    final Long aUserId = 1234L;
    this.details.put(SecurityAuthenticationKeyDetailConstants.USER_ID, aUserId);
    Assert.assertEquals(aUserId, this.classUnderTest.getUserId());
  }

 
  @Test
  public void shouldGetJwtToken() {
    final String aJwtToken = "aJwtToken";
    this.details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN, aJwtToken);
    Assert.assertEquals(aJwtToken, this.classUnderTest.getJwtToken());
  }

  @Test
  public void shouldGetJwtTokenCreationTime() {
    long aJwtTokenCreationTime = 0L;
    this.details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_CREATION_TIME,
        aJwtTokenCreationTime);
    Assert.assertEquals(aJwtTokenCreationTime,
        this.classUnderTest.getJwtTokenCreationTime().toInstant(ZoneOffset.UTC).toEpochMilli());

    aJwtTokenCreationTime = System.currentTimeMillis() / 1000;
    this.details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_CREATION_TIME,
        aJwtTokenCreationTime);
    Assert.assertEquals(aJwtTokenCreationTime * 1000,
        this.classUnderTest.getJwtTokenCreationTime().toInstant(ZoneOffset.UTC).toEpochMilli());
  }

 
  @Test
  public void shouldGetJwtTokenExpirationTime() {
    long aJwtTokenExpirationTime = 0L;
    this.details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_EXPIRE_TIME,
        aJwtTokenExpirationTime);
    Assert.assertEquals(aJwtTokenExpirationTime,
        this.classUnderTest.getJwtTokenExpireTime().toInstant(ZoneOffset.UTC).toEpochMilli());

    aJwtTokenExpirationTime = System.currentTimeMillis() / 1000;
    this.details.put(SecurityAuthenticationKeyDetailConstants.JWT_TOKEN_EXPIRE_TIME,
        aJwtTokenExpirationTime);
    Assert.assertEquals(aJwtTokenExpirationTime * 1000,
        this.classUnderTest.getJwtTokenExpireTime().toInstant(ZoneOffset.UTC).toEpochMilli());
  }

}
