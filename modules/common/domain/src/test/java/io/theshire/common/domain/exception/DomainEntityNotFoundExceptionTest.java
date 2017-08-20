

package io.theshire.common.domain.exception;

import io.theshire.common.domain.DomainObject;

import org.junit.Assert;
import org.junit.Test;


public class DomainEntityNotFoundExceptionTest {

 
  private class TestableDomainEntityNotFoundException extends DomainEntityNotFoundException {

   
    private static final long serialVersionUID = -3518167285580546363L;

   
    public TestableDomainEntityNotFoundException(String errorCode, String message,
        Class<? extends DomainObject> missingClass) {
      super(errorCode, message, missingClass);
    }

  }

 
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIfMissingClassArgIsNull() {
    final TestableDomainEntityNotFoundException shouldThrowException =
        new TestableDomainEntityNotFoundException("errorCode", "message", null);
    Assert.fail("Should have thrown: " + shouldThrowException);
  }
}
