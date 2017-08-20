

package io.theshire.document.domain.user.avatar;

import io.theshire.common.domain.DomainObject;

import lombok.Getter;

import java.net.URL;


public class UserAvatarUploadResult extends DomainObject {


 
  private static final long serialVersionUID = -3157104661938318684L;

 

 
  @Getter
  private final URL uploadedDestination;

 
  public UserAvatarUploadResult(final URL uploadedDestination) {
    this.uploadedDestination = uploadedDestination;
  }

}
