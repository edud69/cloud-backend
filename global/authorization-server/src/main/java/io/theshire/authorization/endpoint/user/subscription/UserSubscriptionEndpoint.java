

package io.theshire.authorization.endpoint.user.subscription;

import io.theshire.authorization.endpoint.user.subscription.adapters.UserSubscriptionActivationAdapter;
import io.theshire.authorization.endpoint.user.subscription.adapters.UserSubscriptionRequestAdapter;
import io.theshire.authorization.endpoint.user.subscription.message.UserSubscriptionActivationRequestMessage;
import io.theshire.authorization.endpoint.user.subscription.message.UserSubscriptionRequestMessage;
import io.theshire.authorization.service.user.subscription.UserSubscriptionService;
import io.theshire.common.domain.exception.DomainException;
import io.theshire.common.endpoint.ManagedRestEndpoint;
import io.theshire.common.service.OutPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/user/subscription", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserSubscriptionEndpoint extends ManagedRestEndpoint {

 
  @Autowired
  private UserSubscriptionService userSubscriptionService;

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.POST)
  public void httpPost(@RequestBody final UserSubscriptionRequestMessage subscriptionRequest)
      throws DomainException {
    userSubscriptionService.processInvitationRequest(
        new UserSubscriptionRequestAdapter(subscriptionRequest), OutPort.ignore());
  }

 
  @ResponseBody
  @RequestMapping(method = RequestMethod.POST, value = "/activation")
  public void httpActivationPost(
      @RequestBody final UserSubscriptionActivationRequestMessage subscriptionRequest)
      throws DomainException {
    userSubscriptionService.processActivationRequest(
        new UserSubscriptionActivationAdapter(subscriptionRequest), OutPort.ignore());
  }
}
