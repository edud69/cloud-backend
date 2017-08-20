

package io.theshire.authorization.domain.impl.user.authentication;

import static org.junit.Assert.assertEquals;

import io.theshire.authorization.domain.AuthorizationErrorCodeConstants;

import org.junit.Test;


public class UserAuthenticationInvalidPasswordFormatExceptionTest {

 
  @Test
  public void setup() {
    final UserAuthenticationInvalidPasswordFormatException classUnderTest =
        new UserAuthenticationInvalidPasswordFormatException("Exception message");
    assertEquals(AuthorizationErrorCodeConstants.PASSWORD_INVALID_FORMAT,
        classUnderTest.getErrorCode());
  }

}
