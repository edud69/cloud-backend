

package io.theshire.authorization.domain.impl.role;

import io.theshire.authorization.domain.role.Role;
import io.theshire.common.domain.DomainObject;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "users_roles")
public class UserRole extends DomainObject {

 
  private static final long serialVersionUID = 8288997937786113925L;

 
  protected UserRole() {
  }

 
  public UserRole(final Long userId, final Role role) {
    this.userId = userId;
    this.role = role;
  }

 
  public String getRolename() {
    return this.role != null ? this.role.getName() : null;
  }

 
  @Getter
  @Column(name = "user_id")
  private Long userId;

 

 

 
  @Getter
  @ManyToOne
  @JoinColumn(name = "role_id")
  private Role role;

}
