

package io.theshire.migration.utils.crypto;

import io.theshire.common.utils.security.encryptor.CryptoUtils;


public class CryptoDataDecoder {

 
  private static volatile CryptoUtils instance;

 
  public static synchronized void initialize(CryptoUtils cryptoUtils) {
    if (instance == null) {
      instance = cryptoUtils;
    }
  }

 
  public static CryptoUtils getCryptoUtils() {
    return instance;
  }

}
