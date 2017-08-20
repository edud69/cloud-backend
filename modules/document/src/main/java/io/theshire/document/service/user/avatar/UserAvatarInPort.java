

package io.theshire.document.service.user.avatar;

import org.springframework.web.multipart.MultipartFile;


@FunctionalInterface
public interface UserAvatarInPort {

 
  MultipartFile getFile();
}
