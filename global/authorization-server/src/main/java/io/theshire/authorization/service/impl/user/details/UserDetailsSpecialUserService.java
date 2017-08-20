

package io.theshire.authorization.service.impl.user.details;

import io.theshire.authorization.domain.user.details.UserDetailsExtended;
import io.theshire.authorization.service.authentication.resquest.details.AuthenticationRequestDetails;
import io.theshire.authorization.service.impl.user.details.UserDetailsSpecialUserMapper.SpecialUserInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsSpecialUserService {

 
  @Autowired
  private UserDetailsSpecialUserMapper userDetailsSpecialUserMapper;

 
  public final UserDetails loadSpecialUser(final String username) {
    final SpecialUserInfo specialUser = userDetailsSpecialUserMapper.getSpecialUser(username);
    if (specialUser != null) {
      return new UserDetailsExtended(-1L, specialUser.getUsername(), specialUser.getPassword(),
          null, true, true, true, true, specialUser.getGas(), AuthenticationRequestDetails.get());
    }
    return null;
  }

}
