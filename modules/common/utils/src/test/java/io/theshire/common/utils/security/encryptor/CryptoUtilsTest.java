

package io.theshire.common.utils.security.encryptor;

import org.junit.Assert;
import org.junit.Test;

public class CryptoUtilsTest {

 
  private CryptoUtils classUnderTest = new CryptoUtils();

 
  @Test
  public void testEncryptDecrypt() throws Exception {
    // key asdfasdf
    final String originalData = "unencrypted";
    classUnderTest.password = "asdfasdf";
    final String actualEncrypted = classUnderTest.encrypt(originalData);
    Assert.assertNotEquals(originalData, actualEncrypted);
    Assert.assertEquals("tdz7MvSPGsMRjtlx0K2Mog==", actualEncrypted);
    Assert.assertEquals(originalData, classUnderTest.decrypt(actualEncrypted));

    // key 0912hjjasda7
    final String originalData2 = "unencrypted";
    classUnderTest = new CryptoUtils("0912hjjasda7");
    final String actualEncrypted2 = classUnderTest.encrypt(originalData2);
    Assert.assertNotEquals(originalData, actualEncrypted2);
    Assert.assertNotEquals(originalData2, actualEncrypted2);
    Assert.assertNotEquals(actualEncrypted, actualEncrypted2);
    Assert.assertEquals("/KxF7My6qwDSKhShdqMW9g==", actualEncrypted2);
    Assert.assertEquals(originalData2, classUnderTest.decrypt(actualEncrypted2));
  }

}
