

package io.theshire.authorization.service.impl.password;

import io.theshire.authorization.domain.password.PasswordLostRequest;
import io.theshire.authorization.domain.password.PasswordLostRequestRepository;
import io.theshire.authorization.domain.password.PasswordUpdater;
import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationNotFoundException;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationRepository;
import io.theshire.authorization.service.password.PasswordChangeInPort;
import io.theshire.authorization.service.password.PasswordLostInPort;
import io.theshire.authorization.service.password.PasswordUpdateService;
import io.theshire.common.domain.email.Email;
import io.theshire.common.domain.email.EmailAddress;
import io.theshire.common.domain.exception.DomainException;
import io.theshire.common.service.OutPort;
import io.theshire.common.service.infrastructure.email.EmailService;
import io.theshire.common.service.infrastructure.email.EmailTemplateMessage;
import io.theshire.common.service.infrastructure.email.EmailTemplateService;
import io.theshire.common.service.infrastructure.impersonification.ImpersonificationService;
import io.theshire.common.utils.jpa.constants.JpaTransactionManagerConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;


@Service
@Transactional(
    transactionManager = JpaTransactionManagerConstants.SINGLE_TENANT_TRANSACTION_MANAGER)
class PasswordUpdateServiceImpl implements PasswordUpdateService {

 
  private static final String PLACEHOLDER_CONFIRMATION_TOKEN = "${CONFIRMATION_TOKEN}";

 
  private static final String PLACEHOLDER_WEBSITE_URL = "${WEBSITE_ROOT_URL}";

 
  private static final String PLACEHOLDER_USER_MAIL = "${USER_EMAIL}";

 
  @Autowired
  private PasswordUpdater passwordUpdater;

 
  @Autowired
  private UserAuthenticationRepository userAuthRepository;

 
  @Autowired
  private PasswordLostRequestRepository passwordLostRequestRepository;

 
  @Autowired
  private ImpersonificationService impersonificationService;

 
  @Autowired
  private EmailTemplateService emailTemplateService;

 
  @Autowired
  private EmailService emailService;

 
  @Value("${app.cloud.mail.user.lostPassword.from}")
  private String emailFrom;

 
  @Value("${app.cloud.auth.specialUser.user.lostPassword.username}")
  private String lostPasswordSpecialUserUsername;

 
  @Value("${app.cloud.auth.specialUser.user.lostPassword.password}")
  private String lostPasswordSpecialUserPassword;

 
  @Value("${app.cloud.mail.user.lostPassword.templateKey}")
  private String emailTemplateKey;

 
  @Value("${app.cloud.client.website.url.root}")
  private String websiteRootUrl;

 

  @Override
  public void processRequest(PasswordChangeInPort input, OutPort<Boolean, ?> output)
      throws DomainException {
    passwordUpdater.process(input.getRequest());
    output.receive(true);
  }


  @Override
  public void processRequest(PasswordLostInPort input, OutPort<Boolean, ?> output)
      throws UserAuthenticationNotFoundException {
    final UserAuthentication user = userAuthRepository.findByUsername(input.getUsername());
    if (user == null) {
      throw new UserAuthenticationNotFoundException("User was not found.");
    }

    PasswordLostRequest passwordLostReq = new PasswordLostRequest(user);
    passwordLostReq = passwordLostRequestRepository.save(passwordLostReq);
    emailService.send(createLostPasswordMail(input.getUsername(),
        passwordLostReq.getConfirmationToken().getToken()));
    output.receive(true);
  }

 
  private Email createLostPasswordMail(final String username, final String confirmationToken) {
    final EmailAddress from = new EmailAddress(username);
    final Set<EmailAddress> to = new HashSet<>();
    to.add(new EmailAddress(username));

    final EmailTemplateMessage template = impersonificationService.callWithImpersonated(
        () -> emailTemplateService.getTemplate(emailTemplateKey), lostPasswordSpecialUserUsername,
        lostPasswordSpecialUserPassword);

    final String body = template.getBody()
        .replace(PLACEHOLDER_CONFIRMATION_TOKEN, confirmationToken)
        .replace(PLACEHOLDER_WEBSITE_URL, websiteRootUrl).replace(PLACEHOLDER_USER_MAIL, username);

    return new Email(template.getSubject(), from, to, null, body, true);
  }

}
