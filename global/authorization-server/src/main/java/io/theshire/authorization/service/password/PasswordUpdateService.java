

package io.theshire.authorization.service.password;

import io.theshire.authorization.domain.user.authentication.UserAuthenticationNotFoundException;
import io.theshire.common.domain.exception.DomainException;
import io.theshire.common.service.OutPort;


public interface PasswordUpdateService {

 
  void processRequest(final PasswordChangeInPort input, final OutPort<Boolean, ?> output)
      throws DomainException;

 
  void processRequest(final PasswordLostInPort input, final OutPort<Boolean, ?> output)
      throws UserAuthenticationNotFoundException;

}
