

package io.theshire.account.endpoint;

import io.theshire.account.domain.Account;
import io.theshire.account.endpoint.message.FullAccountInfoMsg;
import io.theshire.account.service.AccountCreateInPort;
import io.theshire.account.service.AccountGetInPort;
import io.theshire.account.service.AccountService;
import io.theshire.account.service.AccountUpdateInPort;
import io.theshire.common.endpoint.ManagedRestEndpoint;
import io.theshire.common.service.OutPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountEndpoint extends ManagedRestEndpoint {

 
  @Autowired
  private AccountService accountService;

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.GET, value = "/{userId}")
  public ResponseEntity<FullAccountInfoMsg> get(@PathVariable("userId") final Long userId) {
    final AccountGetInPort input = () -> userId;
    final OutPort<Account, FullAccountInfoMsg> output =
        OutPort.create(account -> toRestMessage(account));
    accountService.getByUserId(input, output);
    return buildResponse(output.get());
  }

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<FullAccountInfoMsg> post(@RequestBody final FullAccountInfoMsg account) {
    final AccountCreateInPort input = () -> new Account(account.getUserId(), account.getFirstName(),
        account.getLastName(), account.getGender(), account.getBirthday(), account.getAvatarUrl());
    final OutPort<Account, FullAccountInfoMsg> output =
        OutPort.create(received -> toRestMessage(received));
    accountService.create(input, output);
    return buildResponse(output.get());
  }

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.PUT, value = "/{userId}")
  public ResponseEntity<FullAccountInfoMsg> put(@PathVariable("userId") final Long userId,
      @RequestBody final FullAccountInfoMsg account) {
    account.setUserId(userId); // ignore user ID from message body
    final AccountUpdateInPort input = () -> new Account(account.getUserId(), account.getFirstName(),
        account.getLastName(), account.getGender(), account.getBirthday(), account.getAvatarUrl());
    final OutPort<Account, FullAccountInfoMsg> output =
        OutPort.create(received -> toRestMessage(received));
    accountService.update(input, output);
    return buildResponse(output.get());
  }

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.DELETE, value = "/{userId}")
  public ResponseEntity<String> delete(@PathVariable("userId") final Long userId) {
    accountService.deleteByUserId(() -> userId, OutPort.ignore());
    return buildEmptyResponse();
  }

 
  private FullAccountInfoMsg toRestMessage(final Account input) {
    if (input == null) {
      return null;
    }

    final FullAccountInfoMsg accountInfo = new FullAccountInfoMsg();
    accountInfo.setUserId(input.getUserId());
    accountInfo.setFirstName(input.getFirstName());
    accountInfo.setLastName(input.getLastName());
    accountInfo.setGender(input.getGender());
    accountInfo.setBirthday(input.getBirthday());
    accountInfo.setAvatarUrl(input.getAvatarUrl());
    return accountInfo;
  }

}
