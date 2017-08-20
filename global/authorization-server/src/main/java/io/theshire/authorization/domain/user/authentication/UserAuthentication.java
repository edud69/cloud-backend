

package io.theshire.authorization.domain.user.authentication;

import io.theshire.authorization.domain.impl.user.authentication.UserAuthenticationInvalidPasswordFormatException;
import io.theshire.authorization.domain.permission.Permission;
import io.theshire.authorization.domain.role.Role;
import io.theshire.common.domain.DomainObject;
import io.theshire.common.server.tenant.TenantDatabaseSchema;
import io.theshire.common.utils.security.encryptor.CredentialsEncryptor;

import lombok.Builder;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "users", schema = TenantDatabaseSchema.SINGLE_SIGN_ON_SCHEMA)




@Builder
public class UserAuthentication extends DomainObject implements UserDetails {

 
  private static final long serialVersionUID = 6216152066929706169L;

 
  private static final CredentialsEncryptor credsEncryptor = new CredentialsEncryptor();

  // ( # Start of group
  // (?=.*\d) # must contains one digit from 0-9
  // (?=.*[a-z]) # must contains one lowercase characters
  // (?=.*[A-Z]) # must contains one uppercase characters
  // (?=.*[!@#$%]) # must contains one special symbols in the list "!@#$%"
  // . # match anything with previous condition checking
  // {6,20} # length at least 6 characters and maximum of 20
 
  // ) # End of group
  private static final String PASSWORD_PATTERN =
      "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%]).{6,20})";

 
  private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);

 
  // TODO create username validation logic




  @Getter
  @Column(name = "username")
  private String username;

 



  @Getter
  @Column(name = "password")
  private String password;

 



  @Getter
  @Column(name = "account_non_expired")
  private boolean accountNonExpired;

 



  @Getter
  @Column(name = "account_non_locked")
  private boolean accountNonLocked;

 



  @Getter
  @Column(name = "credentials_non_expired")
  private boolean credentialsNonExpired;

 



  @Getter
  @Column(name = "enabled")
  private boolean enabled;

 

 
  @Getter
  @Column(name = "password_update_time")
  private LocalDateTime passwordUpdateTime;

 
  @Transient
  private Set<Role> roles;

 
  protected void setRoles(final Set<Role> roles) {
    this.roles = roles;
  }

 
  protected UserAuthentication() {
    this.roles = new HashSet<>();
  }

 
  public UserAuthentication(String username, String password, boolean accountNonExpired,
      boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled,
      LocalDateTime passwordUpdateTime, Set<Role> roles) {
    super();
    this.username = username;
    this.password = credsEncryptor.encode(password);
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
    this.enabled = enabled;
    this.passwordUpdateTime = passwordUpdateTime;
    this.roles = roles != null ? roles : new HashSet<>();
  }

 
  public boolean hasRole(final Role role) {
    return roles.contains(role);
  }

 
  public void addRole(final Role role) {
    roles.add(role);
  }

 
  public void removeRole(final Role role) {
    roles.remove(role);
  }

 
  public Set<Role> getRoles() {
    return Collections.unmodifiableSet(roles);
  }

 
  public void enable() {
    this.enabled = true;
  }

 
  private void validatePassword(final String password)
      throws UserAuthenticationInvalidPasswordFormatException {
    if (password == null || !passwordPattern.matcher(password).matches()) {
      throw new UserAuthenticationInvalidPasswordFormatException("Password format is not valid.");
    }
  }

 
  public void changePassword(final String newPassword)
      throws UserAuthenticationInvalidPasswordFormatException {
    validatePassword(newPassword);
    this.password = credsEncryptor.encode(newPassword);
    this.passwordUpdateTime = LocalDateTime.now(Clock.systemUTC());
    this.credentialsNonExpired = true;
  }


  @Override
  public Collection<GrantedAuthority> getAuthorities() {
    final Set<GrantedAuthority> authorities = new HashSet<>();
    final Collection<Permission> privileges = Collections.unmodifiableCollection(roles.stream()
        .map(roles -> roles.getPermissions()).flatMap(p -> p.stream()).collect(Collectors.toSet()));
    authorities.addAll(roles.stream().map(r -> new SimpleGrantedAuthority(r.getName()))
        .collect(Collectors.toSet()));
    authorities.addAll(privileges);
    return Collections.unmodifiableCollection(authorities);
  }
}
