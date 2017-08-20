

package io.theshire.authorization.service.social.oauth2;

import lombok.Data;




@Data
public class OAuth2ConnectionFactoryConfiguration {

 
  private String appId;

 
  private String appSecret;

 
  private String scope;

 
  private String emailDomain;
}
