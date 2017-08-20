

package io.theshire.common.endpoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ApiErrorTest {

 
  private ApiError classUnderTest;

 
  @Before
  public void setup() {
    this.classUnderTest = new ApiError();
  }

 
  @Test
  public void shouldEqualOrNot() {
    final ApiError anotherError = new ApiError();
    anotherError.setCode("0x0001");
    Assert.assertNotEquals(anotherError, classUnderTest);
    classUnderTest.setCode("0x0001");
    Assert.assertEquals(anotherError, classUnderTest);
    anotherError.setMessage("A message");
    Assert.assertNotEquals(anotherError, classUnderTest);
    classUnderTest.setMessage("A message");
    Assert.assertEquals(anotherError, classUnderTest);
  }

}
