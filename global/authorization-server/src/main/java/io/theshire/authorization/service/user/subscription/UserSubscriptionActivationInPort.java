

package io.theshire.authorization.service.user.subscription;


public interface UserSubscriptionActivationInPort {

 
  String getEmail();

 
  String getToken();

 
  String getTenantId();

}
