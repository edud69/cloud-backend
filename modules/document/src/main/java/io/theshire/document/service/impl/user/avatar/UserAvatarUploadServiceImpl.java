

package io.theshire.document.service.impl.user.avatar;

import io.theshire.common.service.OutPort;
import io.theshire.document.domain.user.avatar.UserAvatarUploadException;
import io.theshire.document.domain.user.avatar.UserAvatarUploadProvider;
import io.theshire.document.domain.user.avatar.UserAvatarUploadResult;
import io.theshire.document.domain.user.avatar.UserAvatarUploader;
import io.theshire.document.service.user.avatar.UserAvatarInPort;
import io.theshire.document.service.user.avatar.UserAvatarUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@Service
class UserAvatarUploadServiceImpl implements UserAvatarUploadService {

 
  @Autowired
  private UserAvatarUploadProvider userAvatarUploadProvider;

 
  @Value("${app.cloud.document.user.avatar.maxUploadFileSizeKb}")
  private int maxAllowedFileSizeKb;


  @Override
  public void upload(UserAvatarInPort input, OutPort<UserAvatarUploadResult, ?> output)
      throws UserAvatarUploadException {
    final MultipartFile file = input.getFile();
    try (final InputStream is = file.getInputStream()) {
      final UserAvatarUploader uploader =
          new UserAvatarUploader(userAvatarUploadProvider, maxAllowedFileSizeKb);
      final UserAvatarUploadResult result =
          uploader.upload(file.getOriginalFilename(), file.getContentType(), file.getSize(), is);
      output.receive(result);
    } catch (final IOException exc) {
      throw new RuntimeException(exc);
    }
  }
}
