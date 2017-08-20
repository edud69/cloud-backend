

package io.theshire.authorization.service.impl.user.subcription;

import io.theshire.authorization.domain.role.Role;
import io.theshire.authorization.domain.role.RoleLabel;
import io.theshire.authorization.domain.role.RoleRepository;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationRepository;
import io.theshire.authorization.domain.user.subscription.UserSubscriptionRepository;
import io.theshire.common.service.infrastructure.email.EmailService;
import io.theshire.common.service.infrastructure.email.EmailTemplateService;
import io.theshire.common.service.infrastructure.impersonification.ImpersonificationService;
import io.theshire.common.utils.security.role.constants.SecurityRoleConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;


@Transactional
abstract class UserSubscriptionProcessingService {

 
  @Autowired
  protected EmailService emailService;

 
  @Autowired
  protected UserSubscriptionRepository userSubscriptionRepository;

 
  @Autowired
  protected UserAuthenticationRepository userAuthenticationRepository;

 
  @Autowired
  protected RoleRepository roleRepository;

 
  @Autowired
  protected EmailTemplateService emailTemplateService;

 
  @Autowired
  protected ImpersonificationService impersonificationService;

 
  @Value("${app.cloud.mail.user.sub.from}")
  protected String emailFrom;

 
  @Value("${app.cloud.auth.specialUser.user.sub.username}")
  protected String userSubscriptionUsername;

 
  @Value("${app.cloud.auth.specialUser.user.sub.password}")
  protected String userSubscriptionPassword;

 
  protected Role getNewUserRole() {
    return roleRepository.findByRolename(new RoleLabel(SecurityRoleConstants.NEW_USER));
  }

 
  protected Role getUserRole() {
    return roleRepository.findByRolename(new RoleLabel(SecurityRoleConstants.USER));
  }

}
