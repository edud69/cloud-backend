

package io.theshire.document.domain.user.avatar;

import org.springframework.http.MediaType;


public class UserAvatarUploadValidator {

 
  private final long maxAllowedFileSizeKb;

 
  public UserAvatarUploadValidator(final long maxAllowedFileSizeKb) {
    this.maxAllowedFileSizeKb = maxAllowedFileSizeKb;
  }

 
  public void validate(final long fileSize, final String contentType)
      throws UserAvatarUploadException {
    if (maxAllowedFileSizeKb * 1024 < fileSize) {
      throw new UserAvatarUploadException(
          "File size is too large, max size allowed in KB is " + maxAllowedFileSizeKb + ".");
    }

    final MediaType mediaType = MediaType.parseMediaType(contentType);
    if (!mediaType.equals(MediaType.IMAGE_GIF) && !mediaType.equals(MediaType.IMAGE_JPEG)
        && !mediaType.equals(MediaType.IMAGE_PNG)) {
      throw new UserAvatarUploadException("File must be an image.");
    }
  }

}
