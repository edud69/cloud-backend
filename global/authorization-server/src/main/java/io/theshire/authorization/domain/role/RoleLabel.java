

package io.theshire.authorization.domain.role;

import io.theshire.common.domain.ValueObject;
import io.theshire.common.utils.security.constants.SecurityConstants;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Embeddable
public class RoleLabel extends ValueObject {

 
  private static final long serialVersionUID = -8093238058111221850L;

 
  @NotNull
  @Size(max = 64)
  @Pattern(regexp = "^(" + SecurityConstants.ROLE_PREFIX
      + ").*$", message = "A role must start with ROLE_ prefix.")

 
  @Getter
  @Column(name = "role_name")
  private String roleName;

 
  public RoleLabel(final String roleName) {
    this.roleName = roleName;
  }

 
  protected RoleLabel() {
  }

}
