

package io.theshire.common.utils.security.encryptor;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


@Component
public class CryptoUtils {

 
  @Value("${app.cloud.security.database.encryption.key}")
  protected String password;

 
  private final byte[] salt = { (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, (byte) 0xde,
      (byte) 0x33, (byte) 0x10, (byte) 0x12, };

 
  public CryptoUtils(final String password) {
    this.password = password;
  }

 
  protected CryptoUtils() {

  }

 
  public String encrypt(final String property)
      throws GeneralSecurityException, UnsupportedEncodingException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(salt, 20));
    return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
  }

 
  public String decrypt(final String property) throws GeneralSecurityException, IOException {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
    SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(salt, 20));
    return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
  }

 
  private byte[] base64Decode(String property) throws IOException {
    return Base64.decodeBase64(property);
  }

 
  private String base64Encode(byte[] bytes) {
    return Base64.encodeBase64String(bytes);
  }

}
