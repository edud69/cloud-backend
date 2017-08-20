

package io.theshire.authorization.service.user.subscription;


public interface UserSubscriptionRequestInPort {

 
  String getEmail();

 
  String getPassword();

 
  String getTenantId();

}
