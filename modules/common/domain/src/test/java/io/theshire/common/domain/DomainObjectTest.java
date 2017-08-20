

package io.theshire.common.domain;

import org.junit.Assert;
import org.junit.Test;


public class DomainObjectTest {

 
  @Test
  public void testEquals() throws Exception {
    Assert.assertFalse(new DomainObject() {
      private static final long serialVersionUID = -8410473680420566713L;

    }.equals(null));
  }

 
  @Test
  public void testHashCode() throws Exception {
    Assert.assertEquals(new DomainObject() {
      private static final long serialVersionUID = 5981049596505414209L;
    }.hashCode(), new DomainObject() {
      private static final long serialVersionUID = 5981049596505414209L;
    }.hashCode());
  }

}
