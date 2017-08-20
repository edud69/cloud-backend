

package io.theshire.common.domain.exception;

import io.theshire.common.domain.DomainObject;

import lombok.Getter;

import org.springframework.util.Assert;


public abstract class DomainEntityNotFoundException extends DomainException {

 
  private static final long serialVersionUID = -8037226627306326995L;

 
  @Getter
  private Class<? extends DomainObject> missingClass;

 
  public DomainEntityNotFoundException(String errorCode, String message,
      final Class<? extends DomainObject> missingClass) {
    super(errorCode, message);
    Assert.notNull(missingClass, "missingClass cannot be null.");
    this.missingClass = missingClass;
  }

}
