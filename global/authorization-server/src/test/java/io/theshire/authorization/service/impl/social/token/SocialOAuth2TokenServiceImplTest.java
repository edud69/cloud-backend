

package io.theshire.authorization.service.impl.social.token;

import com.google.common.collect.Lists;

import io.theshire.authorization.domain.social.SocialProvider;
import io.theshire.common.service.OutPort;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionRepository;

import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class SocialOAuth2TokenServiceImplTest {

 
  @Mock
  private ConnectionRepository connectionRepository;

 
  @InjectMocks
  private SocialOAuth2TokenServiceImpl classUnderTest;

 
  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

 
  @Test
  public void shouldReturnAnOAuth2Token() {
    final Connection<?> mockedConnection = Mockito.mock(Connection.class);
    final List<Connection<?>> conns = Lists.newArrayList(mockedConnection);
    Mockito.when(connectionRepository.findConnections(SocialProvider.FACEBOOK.getId()))
        .thenReturn(conns);
    final String aRefreshToken = "refreshTokenValue";
    final ConnectionData mockedConnData = Mockito.mock(ConnectionData.class);
    Mockito.when(mockedConnection.createData()).thenReturn(mockedConnData);
    Mockito.when(mockedConnData.getRefreshToken()).thenReturn(aRefreshToken);
    final OutPort<OAuth2AccessToken, OAuth2AccessToken> output =
        OutPort.create(received -> received);
    classUnderTest.get(() -> SocialProvider.FACEBOOK, output);
    Assert.assertEquals(aRefreshToken, output.get().getRefreshToken().getValue());
  }

 
  @Test
  public void shouldNotReutnrAnOAuth2Token() {
    final List<Connection<?>> conns = Lists.newArrayList();
    Mockito.when(connectionRepository.findConnections(SocialProvider.GOOGLE.getId()))
        .thenReturn(conns);
    final OutPort<OAuth2AccessToken, OAuth2AccessToken> output =
        OutPort.create(received -> received);
    classUnderTest.get(() -> SocialProvider.GOOGLE, output);
    Assert.assertNull(output.get());
  }

}
