

package io.theshire.common.utils.security.encryptor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;


public class CredentialsEncryptor extends BCryptPasswordEncoder {

 
  public CredentialsEncryptor() {
    super(12, new SecureRandom());
  }

}
