

package io.theshire.authorization.service.impl.user.details;

import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationRepository;
import io.theshire.authorization.domain.user.details.UserDetailsExtended;
import io.theshire.authorization.service.authentication.resquest.details.AuthenticationRequestDetails;
import io.theshire.authorization.service.user.details.UserDetailsAuthService;
import io.theshire.common.server.tenant.exception.TenantNotFoundException;
import io.theshire.common.utils.security.authentication.AuthenticationDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UserDetailsAuthServiceImpl implements UserDetailsAuthService {

 
  @Autowired
  private UserAuthenticationRepository userAuthenticationRepository;

 
  @Autowired
  private UserDetailsSpecialUserService userDetailsSpecialUserService;


  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    final UserDetails specialUser = userDetailsSpecialUserService.loadSpecialUser(username);
    if (specialUser != null) {
      return specialUser;
    }

    final String tid = extractTenantIdentifier(AuthenticationRequestDetails.get());
    final UserAuthentication ua = userAuthenticationRepository.findByUsername(username);
    if (ua == null || ua.getAuthorities().isEmpty()) {
      throw new UsernameNotFoundException(
          "Username: " + username + " does not exists in tenant context : " + tid + ".");
    }

    return new UserDetailsExtended(ua.getId(), ua.getUsername(), ua.getPassword(),
        ua.getPasswordUpdateTime(), ua.isEnabled(), ua.isAccountNonExpired(),
        ua.isCredentialsNonExpired(), ua.isAccountNonLocked(), ua.getAuthorities(),
        AuthenticationRequestDetails.get());
  }

 
  private String extractTenantIdentifier(final AuthenticationDetails authDetails) {
    if (authDetails == null) {
      throw new IllegalArgumentException("AuthenticationDetails cannot be null.");
    }
    final String tid = authDetails.getTenantIdentifier();
    if (tid == null) {
      throw new TenantNotFoundException("Tenant cannot be null.");
    }
    return tid;
  }
}
