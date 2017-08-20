

package io.theshire.document.domain.user.avatar;

import java.io.InputStream;
import java.net.URL;


public class UserAvatarUploader {

 
  private final UserAvatarUploadValidator validator;

 
  private UserAvatarUploadProvider provider;

 
  public UserAvatarUploader(final UserAvatarUploadProvider provider,
      final long maxAllowedFileSizeKb) {
    this.provider = provider;
    this.validator = new UserAvatarUploadValidator(maxAllowedFileSizeKb);
  }

 
  public UserAvatarUploadResult upload(final String filename, final String contentType,
      long fileSize, final InputStream avatar) throws UserAvatarUploadException {
    validator.validate(fileSize, contentType);
    final URL uploadedDestination = provider.upload(filename, avatar);
    return new UserAvatarUploadResult(uploadedDestination);
  }

}
