

package io.theshire.document.endpoint.user.avatar;

import io.theshire.common.endpoint.ManagedRestEndpoint;
import io.theshire.common.service.OutPort;
import io.theshire.document.domain.user.avatar.UserAvatarUploadException;
import io.theshire.document.domain.user.avatar.UserAvatarUploadResult;
import io.theshire.document.endpoint.message.FileUploadResultMessage;
import io.theshire.document.service.user.avatar.UserAvatarUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping(value = "/user/avatar", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserAvatarEndpoint extends ManagedRestEndpoint {

 
  @Autowired
  private UserAvatarUploadService userAvatarUploadService;

 
  @RequestMapping(method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<?> httpPost(MultipartFile file) throws UserAvatarUploadException {
    final OutPort<UserAvatarUploadResult, FileUploadResultMessage> output =
        OutPort.create(received -> toRest(received));
    this.userAvatarUploadService.upload(() -> file, output);
    return this.buildResponse(output.get());
  }

 
  private FileUploadResultMessage toRest(final UserAvatarUploadResult uploadResult) {
    final FileUploadResultMessage message = new FileUploadResultMessage();
    message.setUploadedDestination(uploadResult.getUploadedDestination().toString());
    return message;
  }

}
