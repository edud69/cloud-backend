

package io.theshire.authorization.service.impl.social.token;

import io.theshire.authorization.domain.social.SocialOAuth2AccessToken;
import io.theshire.authorization.service.social.token.SocialOAuth2TokenGetInPort;
import io.theshire.authorization.service.social.token.SocialOAuth2TokenService;
import io.theshire.common.service.OutPort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
class SocialOAuth2TokenServiceImpl implements SocialOAuth2TokenService {

 
  @Autowired
  private ConnectionRepository connectionRepository;


  @Override
  public void get(final SocialOAuth2TokenGetInPort input,
      final OutPort<OAuth2AccessToken, ?> output) {
    final Optional<Connection<?>> connection =
        connectionRepository.findConnections(input.getProvider().getId()).stream().findFirst();
    output.receive(connection.isPresent() ? new SocialOAuth2AccessToken(connection.get()) : null);
  }
}
