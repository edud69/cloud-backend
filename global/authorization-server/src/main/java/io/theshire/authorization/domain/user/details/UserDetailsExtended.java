

package io.theshire.authorization.domain.user.details;

import io.theshire.common.utils.security.authentication.AuthenticationDetails;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Collection;


@EqualsAndHashCode(callSuper = true)
public class UserDetailsExtended extends User {

 
  private static final long serialVersionUID = 2109127757932821074L;

 
  private transient AuthenticationDetails authenticationDetails;

 
  @Getter
  private LocalDateTime passwordUpdateTime;

 
  @Getter
  private Long userId;

 
  public UserDetailsExtended(Long userId, String username, String password,
      LocalDateTime passwordUpdateTime, boolean enabled, boolean accountNonExpired,
      boolean credentialsNonExpired, boolean accountNonLocked,
      Collection<? extends GrantedAuthority> authorities,
      AuthenticationDetails authenticationDetails) {
    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
        authorities);
    this.authenticationDetails = authenticationDetails;
    this.passwordUpdateTime = passwordUpdateTime;
    this.userId = userId;
  }

 
  public String getTenantIdentifier() {
    return authenticationDetails.getTenantIdentifier();
  }

}
