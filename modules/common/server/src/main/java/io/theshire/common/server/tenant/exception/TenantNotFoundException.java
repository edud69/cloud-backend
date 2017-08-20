

package io.theshire.common.server.tenant.exception;

import org.springframework.security.core.AuthenticationException;


public class TenantNotFoundException extends AuthenticationException {

 
  private static final long serialVersionUID = -2753285196801101808L;

 
  public TenantNotFoundException(String msg) {
    super(msg);
  }

 
  public TenantNotFoundException(String msg, Throwable throwable) {
    super(msg, throwable);
  }

}
