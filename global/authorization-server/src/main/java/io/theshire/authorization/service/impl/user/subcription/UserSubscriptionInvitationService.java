

package io.theshire.authorization.service.impl.user.subcription;

import io.theshire.authorization.domain.impl.user.authentication.UserAuthenticationInvalidPasswordFormatException;
import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionAlreadyExistsException;
import io.theshire.authorization.domain.role.Role;
import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.authorization.domain.user.subscription.UserSubscription;
import io.theshire.authorization.domain.user.subscription.UserSubscriptionInvitation;
import io.theshire.authorization.domain.user.subscription.UserSubscriptionInvitationRepository;
import io.theshire.authorization.service.authentication.resquest.details.AuthenticationRequestDetails;
import io.theshire.common.domain.email.EmailAddress;
import io.theshire.common.service.infrastructure.email.EmailTemplateMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;


@Service
@Transactional
class UserSubscriptionInvitationService extends UserSubscriptionProcessingService {

 
  @Autowired
  private UserSubscriptionInvitationRepository userSubscriptionInvitationRepository;

 
  @Value("${app.cloud.mail.user.sub.invite.templateKey}")
  private String emailTemplateKey;

 
  @Value("${app.cloud.client.website.url.root}")
  private String clientWebsiteRootUrl;

 
  public void process(final String email, final String password, final String tenantId)
      throws UserSubscriptionAlreadyExistsException,
      UserAuthenticationInvalidPasswordFormatException {
    Assert.notNull(tenantId, "tenantId cannot be null.");
    Assert.isTrue(tenantId.equals(AuthenticationRequestDetails.get().getTenantIdentifier()),
        "Tenant id in the request object must match the current request tenant id.");

    final UserAuthentication userAuth = registerOrGet(email, password);
    checkNoSubscription(userAuth);

    final UserSubscriptionInvitation invitation = buildInvitation(email);
    final UserSubscription userSub =
        userSubscriptionRepository.save(new UserSubscription(invitation, userAuth));
    final UserSubscriptionInvitation emailToSend = userSub.getSubscriptionInvitation();

    sendSubscriptionInvitationEmail(emailToSend);
  }

 
  private void checkNoSubscription(final UserAuthentication userAuth)
      throws UserSubscriptionAlreadyExistsException {
    final UserSubscription userSubscription =
        userSubscriptionRepository.findByUserAuthentication(userAuth);
    if (userSubscription != null) {
      throw new UserSubscriptionAlreadyExistsException("User has already a subscription.");
    }
  }

 
  private void sendSubscriptionInvitationEmail(final UserSubscriptionInvitation email) {
    impersonificationService.runWithImpersonated(() -> {
      userSubscriptionInvitationRepository.save(emailService.send(email));
    }, userSubscriptionUsername, userSubscriptionPassword);
  }

 
  private UserAuthentication registerOrGet(final String username, final String password)
      throws UserAuthenticationInvalidPasswordFormatException {
    final Role newUserSubRole = getNewUserRole();
    UserAuthentication userAuth = userAuthenticationRepository.findByUsername(username);

    if (userAuth == null) {
      userAuth = registerNewUserAuthentication(username, password, newUserSubRole);
    } else if (!userAuth.hasRole(newUserSubRole) && userAuth.getRoles().isEmpty()) {
      // user is new in this tenant context, we do not need to update the password
      Assert.isNull(password, "In this case password should be null user has already a "
          + "Single-Sign-On account password.");
      userAuth.addRole(newUserSubRole);
      userAuth = userAuthenticationRepository.save(userAuth);
    }

    return userAuth;
  }

 
  private UserAuthentication registerNewUserAuthentication(final String username,
      final String password, final Role newUserSubRole)
      throws UserAuthenticationInvalidPasswordFormatException {
    final UserAuthentication userAuth =
        UserAuthentication.builder().accountNonExpired(true).accountNonLocked(true).password("")
            .credentialsNonExpired(false).enabled(false).username(username).build();
    userAuth.changePassword(password);
    userAuth.addRole(newUserSubRole);
    return userAuthenticationRepository.save(userAuth);
  }

 
  private UserSubscriptionInvitation buildInvitation(final String email) {
    final EmailAddress from = new EmailAddress(emailFrom);
    final Set<EmailAddress> to = new HashSet<>();
    to.add(new EmailAddress(email));

    final EmailTemplateMessage template = impersonificationService.callWithImpersonated(
        () -> emailTemplateService.getTemplate(emailTemplateKey), userSubscriptionUsername,
        userSubscriptionPassword);

    return new UserSubscriptionInvitation(template.getSubject(), from, to, null, template.getBody(),
        clientWebsiteRootUrl, true);
  }

}
