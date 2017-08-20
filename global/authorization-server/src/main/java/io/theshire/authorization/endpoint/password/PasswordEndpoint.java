

package io.theshire.authorization.endpoint.password;

import io.theshire.authorization.domain.password.PasswordChangeRequest;
import io.theshire.authorization.domain.password.PasswordRestoreRequest;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationNotFoundException;
import io.theshire.authorization.endpoint.password.message.PasswordChangeRequestMessage;
import io.theshire.authorization.endpoint.password.message.PasswordLostRequestMessage;
import io.theshire.authorization.service.password.PasswordChangeInPort;
import io.theshire.authorization.service.password.PasswordLostInPort;
import io.theshire.authorization.service.password.PasswordUpdateService;
import io.theshire.common.domain.exception.DomainException;
import io.theshire.common.endpoint.ManagedRestEndpoint;
import io.theshire.common.service.OutPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/password", produces = MediaType.APPLICATION_JSON_VALUE)
public class PasswordEndpoint extends ManagedRestEndpoint {

 
  @Autowired
  private PasswordUpdateService passwordUpdateService;

 
  @ResponseBody
  @RequestMapping(value = "/lost", method = RequestMethod.POST)
  public ResponseEntity<String>
      httpPostLostPasswordRequest(@RequestBody final PasswordLostRequestMessage req)
          throws UserAuthenticationNotFoundException {
    final PasswordLostInPort input = () -> req.getUsername();
    passwordUpdateService.processRequest(input, OutPort.ignore());
    return buildEmptyResponse();
  }

 
  @ResponseBody
  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public ResponseEntity<String> httpPostForPasswordChangeRequest(
      @RequestBody final PasswordChangeRequestMessage req) throws DomainException {
    req.setUseLostPasswordToken(false);
    final PasswordChangeInPort input = buildRequest(req);
    passwordUpdateService.processRequest(input, OutPort.ignore());
    return buildEmptyResponse();
  }

 
  @ResponseBody
  @RequestMapping(value = "/restore", method = RequestMethod.POST)
  public ResponseEntity<String> httpPostRestorePasswordRequest(
      @RequestBody final PasswordChangeRequestMessage req) throws DomainException {
    req.setUseLostPasswordToken(true);
    final PasswordChangeInPort input = buildRequest(req);
    passwordUpdateService.processRequest(input, OutPort.ignore());
    return buildEmptyResponse();
  }

 
  private PasswordChangeInPort buildRequest(final PasswordChangeRequestMessage message) {
    return message.isUseLostPasswordToken()
        ? () -> new PasswordRestoreRequest(message.getUsername(), message.getNewPassword(),
            message.getLostPasswordToken())
        : () -> new PasswordChangeRequest(message.getUsername(), message.getNewPassword(),
            message.getOldPassword());
  }

}
