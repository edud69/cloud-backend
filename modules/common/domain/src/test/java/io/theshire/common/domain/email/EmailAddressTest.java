

package io.theshire.common.domain.email;

import org.junit.Assert;
import org.junit.Test;


public class EmailAddressTest {

 
  @Test
  public void shouldThrowWhenInvalidEmail() {
    isInvalidEmail("emmanuel.hibernate.org");
    isInvalidEmail("emma nuel@hibernate.org");
    isInvalidEmail("emma(nuel@hibernate.org");
    isInvalidEmail("emmanuel@");
    isInvalidEmail("emma\nnuel@hibernate.org");
    isInvalidEmail("emma@nuel@hibernate.org");
    isInvalidEmail("Just a string");
    isInvalidEmail("string");
    isInvalidEmail("me@");
    isInvalidEmail("@example.com");
    isInvalidEmail("me.@example.com");
    isInvalidEmail(".me@example.com");
    isInvalidEmail("me@example..com");
    isInvalidEmail("me\\@example.com");
  }

 
  @Test
  public void shouldBeOkWhenValidEmail() {
    isValidEmail("emmanuel@hibernate.org");
    isValidEmail("emmanuel@hibernate.com");
    isValidEmail("emma-n_uel@hibernate.com");
    isValidEmail("emma+nuel@hibernate.org");
    isValidEmail("emma=nuel@hibernate.org");
    isValidEmail("emmanuel@[123.12.2.11]");
    isValidEmail("*@example.net");
    isValidEmail("fred&barny@example.com");
    isValidEmail("---@example.com");
    isValidEmail("foo-bar@example.net");
    isValidEmail("mailbox.sub1.sub2@this-domain.com");
  }

 
  private void isInvalidEmail(final String email) {
    try {
      final EmailAddress addr = new EmailAddress(email);
      Assert.fail("Should throw from EmailAddress! Mail: " + addr);
    } catch (final IllegalArgumentException exc) {
      // OK!
    }
  }

 
  private void isValidEmail(final String email) {
    final EmailAddress addr = new EmailAddress(email);
    Assert.assertEquals(addr.getEmail(), email);
  }

}
