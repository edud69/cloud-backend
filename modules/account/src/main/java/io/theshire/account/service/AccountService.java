

package io.theshire.account.service;

import static io.theshire.common.utils.security.permission.constants.SecurityAccountMicroservicePermissionConstants.ACCOUNT_CREATE;
import static io.theshire.common.utils.security.permission.constants.SecurityAccountMicroservicePermissionConstants.ACCOUNT_DELETE;
import static io.theshire.common.utils.security.permission.constants.SecurityAccountMicroservicePermissionConstants.ACCOUNT_READ;
import static io.theshire.common.utils.security.permission.constants.SecurityAccountMicroservicePermissionConstants.ACCOUNT_UPDATE;

import io.theshire.account.domain.Account;
import io.theshire.common.service.OutPort;

import org.springframework.security.access.prepost.PreAuthorize;


public interface AccountService {

 
  @PreAuthorize("hasPermission(#input, '" + ACCOUNT_CREATE + "')")
  void create(final AccountCreateInPort input, OutPort<Account, ?> output);

 
  @PreAuthorize("hasPermission(#input, '" + ACCOUNT_READ + "')")
  void getByUserId(final AccountGetInPort input, OutPort<Account, ?> output);

 
  @PreAuthorize("hasPermission(#input, '" + ACCOUNT_UPDATE + "')")
  void update(final AccountUpdateInPort input, OutPort<Account, ?> output);

 
  @PreAuthorize("hasPermission(#input, '" + ACCOUNT_DELETE + "')")
  void deleteByUserId(final AccountDeleteInPort input, OutPort<Boolean, ?> output);

}