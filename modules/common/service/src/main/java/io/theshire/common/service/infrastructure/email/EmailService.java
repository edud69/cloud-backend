

package io.theshire.common.service.infrastructure.email;

import io.theshire.common.domain.email.Email;
import io.theshire.common.utils.security.permission.constants.SecurityPermissionConstants;

import org.springframework.security.access.prepost.PreAuthorize;


public interface EmailService {

 
  @PreAuthorize("hasPermission(#email, '" + SecurityPermissionConstants.SEND_EMAIL + "')")
  <T extends Email> T send(final T email);

}
