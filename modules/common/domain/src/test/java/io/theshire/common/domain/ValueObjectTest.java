

package io.theshire.common.domain;

import org.junit.Assert;
import org.junit.Test;


public class ValueObjectTest {

 
  @Test
  public void testHashCode() throws Exception {
    Assert.assertEquals(new ValueObject() {
      private static final long serialVersionUID = 1L;
    }.hashCode(), new ValueObject() {
      private static final long serialVersionUID = 2L;
    }.hashCode());
  }

 
  @Test
  public void testEquals() throws Exception {
    final ValueObject valueObject = new ValueObject() {
      private static final long serialVersionUID = -529524758710715897L;
    };

    Assert.assertEquals(valueObject, valueObject);

    Assert.assertNotEquals(new ValueObject() {
      private static final long serialVersionUID = 1L;
    }, new ValueObject() {
      private static final long serialVersionUID = 2L;
    });
  }

}
