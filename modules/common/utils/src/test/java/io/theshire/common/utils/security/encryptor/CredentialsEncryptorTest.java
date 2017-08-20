

package io.theshire.common.utils.security.encryptor;

import org.junit.Assert;
import org.junit.Test;


public class CredentialsEncryptorTest {

 
  private final CredentialsEncryptor classUnderTest = new CredentialsEncryptor();

 
  @Test
  public void encodes_and_matches() {
    final String rawPassword = "asdfasdf";
    final String encodedPassword = classUnderTest.encode(rawPassword);
    Assert.assertNotNull(encodedPassword);
    Assert.assertNotSame(rawPassword, encodedPassword);
    Assert.assertTrue(classUnderTest.matches(rawPassword, encodedPassword));

    final String anotherPassword = "asdfasdf2";
    Assert.assertFalse(classUnderTest.matches(anotherPassword, encodedPassword));
  }

}
