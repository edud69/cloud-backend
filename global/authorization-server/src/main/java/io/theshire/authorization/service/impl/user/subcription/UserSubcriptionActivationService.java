

package io.theshire.authorization.service.impl.user.subcription;

import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionActivationException;
import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionNotFoundException;
import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.authorization.domain.user.subscription.UserSubscription;
import io.theshire.authorization.service.authentication.resquest.details.AuthenticationRequestDetails;
import io.theshire.common.domain.email.Email;
import io.theshire.common.domain.email.EmailAddress;
import io.theshire.common.service.infrastructure.email.EmailTemplateMessage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;


@Service
@Transactional




@Slf4j
class UserSubcriptionActivationService extends UserSubscriptionProcessingService {

 
  @Value("${app.cloud.mail.user.sub.activation.templateKey}")
  private String emailTemplateKey;

 
  public void process(final String email, final String confirmationToken, final String tenantId)
      throws UserSubscriptionNotFoundException, UserSubscriptionActivationException {
    log.debug("An activation request for a user subscription is attempted, email={}.", email);
    Assert.notNull(tenantId, "tenantId cannot be null.");
    Assert.isTrue(tenantId.equals(AuthenticationRequestDetails.get().getTenantIdentifier()),
        "Tenant id in the request object must match the current request tenant id.");
    Assert.notNull(confirmationToken, "confirmationToken cannot be null.");
    Assert.notNull(email, "email cannot be null.");

    final UserAuthentication userAuth = retrieveUser(email);
    final UserSubscription userSub = retrieveUserSubscription(userAuth);
    userSub.activate(confirmationToken);

    userSubscriptionRepository.save(userSub);

    giveDefaultRolesToUser(userAuth);
    userAuth.enable();

    userAuthenticationRepository.save(userAuth);

    sendActivationEmail(email);
  }

 
  private void sendActivationEmail(final String recipient) {
    impersonificationService.runWithImpersonated(() -> {
      final EmailTemplateMessage template = emailTemplateService.getTemplate(emailTemplateKey);
      final Set<EmailAddress> to = new HashSet<>();
      to.add(new EmailAddress(recipient));
      final Email email = new Email(template.getSubject(), new EmailAddress(emailFrom), to, null,
          template.getBody(), true);
      emailService.send(email);
    }, userSubscriptionUsername, userSubscriptionPassword);
  }

 
  private void giveDefaultRolesToUser(final UserAuthentication userAuth) {
    userAuth.removeRole(getNewUserRole());
    userAuth.addRole(getUserRole());
  }

 
  private UserSubscription retrieveUserSubscription(final UserAuthentication userAuth)
      throws UserSubscriptionNotFoundException {
    final UserSubscription userSub = userSubscriptionRepository.findByUserAuthentication(userAuth);
    if (userSub == null) {
      throw new UserSubscriptionNotFoundException(
          "UserSubscription was not found for userId=" + userAuth.getId() + ".");
    }
    return userSub;
  }

 
  private UserAuthentication retrieveUser(final String username) {
    final UserAuthentication userAuth = userAuthenticationRepository.findByUsername(username);
    if (userAuth == null) {
      throw new UsernameNotFoundException("User was not found.");
    }
    return userAuth;
  }

}
