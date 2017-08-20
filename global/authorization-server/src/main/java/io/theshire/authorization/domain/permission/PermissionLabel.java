

package io.theshire.authorization.domain.permission;

import io.theshire.common.domain.ValueObject;
import io.theshire.common.utils.security.constants.SecurityConstants;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Embeddable
public class PermissionLabel extends ValueObject {

 
  private static final long serialVersionUID = 5464563046897893921L;

 
  @Getter
  @NotNull
  @Size(max = 64)
  @Pattern(regexp = "^(?!" + SecurityConstants.ROLE_PREFIX
      + ").+", message = "A privilege cannot start with ROLE_ prefix.")
  @Column(name = "permission_name")
  private String permissionName;

 
  public PermissionLabel(final String permissionName) {
    this.permissionName = permissionName;
  }

 
  protected PermissionLabel() {
  }

}
