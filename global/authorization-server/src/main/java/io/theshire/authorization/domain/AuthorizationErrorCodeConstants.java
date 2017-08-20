

package io.theshire.authorization.domain;


public class AuthorizationErrorCodeConstants {

 
  public static final String JWT_TOKEN_REVOKED = "EC-auth-0x0001";

 
  public static final String PASSWORD_CHANGE_OTHER_USER_PASS_FORBIDDEN = "EC-auth-0x0002";

 
  public static final String PASSWORD_CHANGE_PREVIOUS_PASS_MISMATCH = "EC-auth-0x0003";

 
  public static final String PASSWORD_NO_LOST_PASS_REQUEST_FOUND = "EC-auth-0x0004";

 
  public static final String PASSWORD_INVALID_FORMAT = "EC-auth-0x0005";

 
  public static final String USERSUB_NOT_READY_FOR_ACTIVATION = "EC-auth-0x0006";

 
  public static final String USERSUB_NO_CONFIRMATION_TOKEN = "EC-auth-0x0007";

 
  public static final String USERSUB_INVALID_CONFIRMATION_TOKEN = "EC-auth-0x0008";

 
  public static final String USERSUB_ALREADY_EXISTS = "EC-auth-0x0009";

 
  public static final String USERSUB_NOT_FOUND = "EC-auth-0x0009";

 
  public static final String USERAUTH_NOT_FOUND = "EC-auth-0x0010";

}
