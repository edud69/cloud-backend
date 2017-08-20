

package io.theshire.account.service.lite;

import static io.theshire.common.utils.security.permission.constants.SecurityAccountMicroservicePermissionConstants.ACCOUNT_READ;

import io.theshire.account.domain.Account;
import io.theshire.common.service.OutPort;

import org.springframework.security.access.prepost.PreAuthorize;


public interface AccountLiteService {

 
  @PreAuthorize("hasPermission(#input, '" + ACCOUNT_READ + "')")
  void get(final AccountLiteGetInPort input, final OutPort<Account, ?> output);

}
