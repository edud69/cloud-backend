

package io.theshire.document.domain.user.avatar;

import io.theshire.common.domain.exception.DomainException;
import io.theshire.document.domain.DocumentErrorCodeConstants;


public class UserAvatarUploadException extends DomainException {

 
  private static final long serialVersionUID = 501783922606152011L;

 
  public UserAvatarUploadException(final String message) {
    super(DocumentErrorCodeConstants.USER_AVATAR_UPLOAD_FAIL, message);
  }

}
