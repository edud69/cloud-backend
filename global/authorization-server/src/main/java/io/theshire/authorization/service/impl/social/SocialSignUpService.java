

package io.theshire.authorization.service.impl.social;

import io.theshire.authorization.domain.role.Role;
import io.theshire.authorization.domain.role.RoleLabel;
import io.theshire.authorization.domain.role.RoleRepository;
import io.theshire.authorization.domain.social.SocialProvider;
import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationRepository;
import io.theshire.authorization.service.social.oauth2.OAuth2Providers;
import io.theshire.common.utils.security.role.constants.SecurityRoleConstants;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional


@Slf4j
public class SocialSignUpService implements ConnectionSignUp {

 
  @Autowired
  private UserAuthenticationRepository userAuthenticationRepository;

 
  @Autowired
  private RoleRepository roleRepository;

 
  @Autowired
  private OAuth2Providers oauth2Providers;


  @Override
  public String execute(final Connection<?> connection) {
    if (log.isDebugEnabled()) {
      log.debug(
          "Social authentication does not exists. "
              + "A signup will be attempted. providerId={}, username={}.",
          connection.getKey().getProviderId(), connection.getKey().getProviderUserId());
    }

    final String username = retrieveEmail(connection);
    UserAuthentication account = userAuthenticationRepository.findByUsername(username);
    final Role r = roleRepository.findByRolename(new RoleLabel(SecurityRoleConstants.SOCIAL_USER));

    if (account == null) {
      if (log.isDebugEnabled()) {
        log.debug("Creating a new account for social user, providerId={}, username={}.",
            connection.getKey().getProviderId(), connection.getKey().getProviderUserId());
      }

      account = UserAuthentication.builder().accountNonExpired(true).accountNonLocked(true)
          .credentialsNonExpired(true).enabled(true).password(KeyGenerators.string().generateKey())
          .username(username).build();
    }

    account.addRole(r);

    return userAuthenticationRepository.save(account).getUsername();
  }

 
  private String retrieveEmail(final Connection<?> connection) {
    final ConnectionKey key = connection.getKey();
    final String providerId = key.getProviderId();
    final String userId = key.getProviderUserId();
    final SocialProvider provider = SocialProvider.fromId(providerId);
    return userId + oauth2Providers.get(provider).getEmailDomain();
  }

}
