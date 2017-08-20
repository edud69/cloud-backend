

package io.theshire.authorization.domain.permission;

import io.theshire.common.domain.DomainObject;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "permissions")
public class Permission extends DomainObject implements GrantedAuthority {

 
  private static final long serialVersionUID = -6615091567140594609L;

 
  @NotNull
  @Embedded
  private PermissionLabel label;

 
  public Permission(final PermissionLabel label) {
    this.label = label;
  }

 
  protected Permission() {
  }

 
  public String getName() {
    return label.getPermissionName();
  }


  @Override
  public String getAuthority() {
    return getName();
  }

}
