

package io.theshire.document.service.user.avatar;

import io.theshire.common.service.OutPort;
import io.theshire.common.utils.security.permission.constants.SecurityDocumentMicroservicePermissionConstants;
import io.theshire.document.domain.user.avatar.UserAvatarUploadException;
import io.theshire.document.domain.user.avatar.UserAvatarUploadResult;

import org.springframework.security.access.prepost.PreAuthorize;


public interface UserAvatarUploadService {

 
  @PreAuthorize("hasPermission(#input, '"
      + SecurityDocumentMicroservicePermissionConstants.USER_AVATAR_UPLOAD + "')")
  void upload(final UserAvatarInPort input, final OutPort<UserAvatarUploadResult, ?> output)
      throws UserAvatarUploadException;

}
