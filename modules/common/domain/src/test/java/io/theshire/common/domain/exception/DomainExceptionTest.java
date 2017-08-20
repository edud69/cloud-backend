

package io.theshire.common.domain.exception;

import org.junit.Assert;
import org.junit.Test;


public class DomainExceptionTest {

 
  public class TestableDomainException extends DomainException {

   
    private static final long serialVersionUID = 343118444354438153L;

   
    public TestableDomainException(String errorCode, String message) {
      super(errorCode, message);
    }

  }

 
  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIfNullErrorCode() {
    final DomainException shouldThrowException = new TestableDomainException(null, "A message.");
    Assert.fail("Should have thrown : " + shouldThrowException);
  }

}
