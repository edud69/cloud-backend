

package io.theshire.authorization.domain.password;

import lombok.Getter;

import org.springframework.util.Assert;


public class PasswordChangeRequest extends PasswordUpdateRequest {

 
  private static final long serialVersionUID = -8053331943377420013L;

 

 
  @Getter
  private String previousPassword;

 
  @Override
  public boolean isLostPasswordRestoreRequest() {
    return false;
  }

 
  public PasswordChangeRequest(final String forUser, final String newPasswordToSet,
      final String previousPassword) {
    super(forUser, newPasswordToSet);
    Assert.notNull(previousPassword, "previousPassword cannot be null.");
    this.previousPassword = previousPassword;
  }

}
