

package io.theshire.common.domain.exception;

import lombok.Getter;

import org.springframework.util.Assert;

import java.util.Map;


public abstract class DomainException extends Exception {

 
  private static final long serialVersionUID = -4268157927015668525L;

 
 
  @Getter
  private String errorCode;

 
  @Getter
  private Map<String, Object> errorParams;

 
  public DomainException(final String errorCode, final String message) {
    this(errorCode, (Map<String, Object>) null, message);
  }

 
  public DomainException(final String errorCode, final Map<String, Object> errorParams,
      final String message) {
    this(errorCode, errorParams, message, null);
  }

 
  public DomainException(final String errorCode, final String message, Throwable exception) {
    this(errorCode, null, message, exception);
  }

 
  public DomainException(final String errorCode, final Map<String, Object> errorParams,
      final String message, Throwable exception) {
    super(message, exception);
    Assert.notNull(errorCode, "errorCode cannot be null.");
    this.errorCode = errorCode;
    this.errorParams = errorParams;
  }

}
