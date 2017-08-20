

package io.theshire.account.endpoint.lite;

import io.theshire.account.domain.Account;
import io.theshire.account.endpoint.message.AccountInfoLiteMsg;
import io.theshire.account.service.lite.AccountLiteService;
import io.theshire.common.endpoint.ManagedRestEndpoint;
import io.theshire.common.service.OutPort;
import io.theshire.common.utils.security.authentication.AuthenticationContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/lite", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountEndpointLite extends ManagedRestEndpoint {

 
  @Autowired
  private AccountLiteService accountLiteService;

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<AccountInfoLiteMsg> get() {
    final OutPort<Account, AccountInfoLiteMsg> output =
        OutPort.create(received -> toRest(received));
    accountLiteService.get(() -> AuthenticationContext.get().getUserId(), output);
    return buildResponse(output.get());
  }

 
  private AccountInfoLiteMsg toRest(final Account account) {
    if (account == null) {
      return null;
    }

    final AccountInfoLiteMsg accountLite = new AccountInfoLiteMsg();
    accountLite.setFirstName(account.getFirstName());
    accountLite.setLastName(account.getLastName());
    accountLite.setUserId(account.getUserId());
    accountLite.setAvatarUrl(account.getAvatarUrl());
    return accountLite;
  }

}
