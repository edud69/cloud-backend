

package io.theshire.authorization.domain.password;

import io.theshire.common.domain.DomainObject;

import lombok.Getter;

import org.springframework.util.Assert;


public abstract class PasswordUpdateRequest extends DomainObject {

 
  private static final long serialVersionUID = -5512464840575641298L;

 
  private String forUser;

 
  @Getter
  private String newPasswordToSet;

 
  public abstract boolean isLostPasswordRestoreRequest();

 
  public String getRequestedUsername() {
    return forUser;
  }

 
  public PasswordUpdateRequest(final String forUser, final String newPasswordToSet) {
    this(forUser);
    Assert.notNull(newPasswordToSet, "newPasswordToSet cannot be null.");
    this.newPasswordToSet = newPasswordToSet;
  }

 
  private PasswordUpdateRequest(final String forUser) {
    super();
    Assert.notNull(forUser, "forUser cannot be null.");
    this.forUser = forUser;
  }

}
