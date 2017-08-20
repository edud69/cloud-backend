

package io.theshire.authorization.service.authentication.resquest.details;

import io.theshire.common.utils.security.authentication.AuthenticationDetails;


public final class AuthenticationRequestDetails {

 
  private static final ThreadLocal<AuthenticationDetails> requestDetails = new ThreadLocal<>();

 
  public static AuthenticationDetails get() {
    return requestDetails.get();
  }

 
  protected static void set(final AuthenticationDetails details) {
    if (details != null) {
      requestDetails.set(details);
    }
  }

 
  protected static void clear() {
    requestDetails.remove();
  }

}
