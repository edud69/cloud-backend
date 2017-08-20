

package io.theshire.authorization.service.password;

import io.theshire.authorization.domain.password.PasswordUpdateRequest;


@FunctionalInterface
public interface PasswordChangeInPort {

 
  PasswordUpdateRequest getRequest();

}
