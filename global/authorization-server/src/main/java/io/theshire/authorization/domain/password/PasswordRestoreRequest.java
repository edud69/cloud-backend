

package io.theshire.authorization.domain.password;

import lombok.Getter;

import org.springframework.util.Assert;


public class PasswordRestoreRequest extends PasswordUpdateRequest {

 
  private static final long serialVersionUID = 6602023554097728933L;

 
  @Getter
  private String confirmationToken;

 
  @Override
  public boolean isLostPasswordRestoreRequest() {
    return true;
  }

 
  public PasswordRestoreRequest(final String forUser, final String newPasswordToSet,
      final String confirmationToken) {
    super(forUser, newPasswordToSet);
    Assert.notNull(confirmationToken, "confirmationToken cannot be null.");
    this.confirmationToken = confirmationToken;
  }

}
