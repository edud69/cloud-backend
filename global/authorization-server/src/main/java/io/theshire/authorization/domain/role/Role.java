

package io.theshire.authorization.domain.role;

import io.theshire.authorization.domain.permission.Permission;
import io.theshire.common.domain.DomainObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "roles")
public class Role extends DomainObject {

 
  private static final long serialVersionUID = 1787699674743854338L;

 
  @NotNull
  @Embedded
  private RoleLabel label;

 
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "roles_permissions",
      joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id"))
  private Set<Permission> permissions = new HashSet<>();

 
  public Role(final RoleLabel label) {
    this.label = label;
  }

 
  protected Role() {
  }

 
  public String getName() {
    return label.getRoleName();
  }

 
  public Set<Permission> getPermissions() {
    return Collections.unmodifiableSet(permissions);
  }

 
  public boolean hasPermission(final Permission privilege) {
    return permissions.contains(privilege);
  }

 
  public void addPermission(final Permission permission) {
    permissions.add(permission);
  }

 
  public void removePermission(final Permission permission) {
    permissions.remove(permission);
  }

}
