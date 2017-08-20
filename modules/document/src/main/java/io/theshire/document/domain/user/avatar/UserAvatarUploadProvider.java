

package io.theshire.document.domain.user.avatar;

import java.io.InputStream;
import java.net.URL;


public interface UserAvatarUploadProvider {

 
  URL upload(final String filename, final InputStream avatar) throws UserAvatarUploadException;
}
