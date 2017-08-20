

package io.theshire.authorization.domain.impl.password;

import com.google.common.collect.Sets;

import io.theshire.authorization.domain.password.PasswordLostRequest;
import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationRepository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;


@RunWith(MockitoJUnitRunner.class)
public class PasswordLostRequestRepositoryImplTest {

 
  @Mock
  private PasswordLostRequestJpaSingleTenantRepository passwordLostRequestJpaRepository;

 
  @Mock
  private UserAuthenticationRepository userAuthRepository;

 
  @InjectMocks
  private PasswordLostRequestRepositoryImpl classUnderTest;

 
  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

 
  @Test
  public void shouldFindById() {
    final PasswordLostRequest mockedResult = Mockito.mock(PasswordLostRequest.class);
    Mockito.when(passwordLostRequestJpaRepository.findOne(1L)).thenReturn(mockedResult);
    final PasswordLostRequest request = this.classUnderTest.findById(1L);
    Assert.assertEquals(mockedResult, request);
  }

 
  @Test
  public void shouldSave() {
    final PasswordLostRequest mockedResult = Mockito.mock(PasswordLostRequest.class);
    final PasswordLostRequest mockedSavedResult = Mockito.mock(PasswordLostRequest.class);
    Mockito.when(passwordLostRequestJpaRepository.save(mockedResult)).thenReturn(mockedSavedResult);
    Mockito.when(mockedSavedResult.getId()).thenReturn(1L);
    final PasswordLostRequest result = classUnderTest.save(mockedResult);
    Mockito.verify(passwordLostRequestJpaRepository).save(mockedResult);
    Assert.assertNotNull(result.getId());
  }

 
  @Test
  public void shouldFindByRequestedUsername() {
    final String username = "ausername";
    final UserAuthentication userAuth = Mockito.mock(UserAuthentication.class);
    Mockito.when(userAuthRepository.findByUsername(username)).thenReturn(userAuth);
    Mockito.when(userAuth.getId()).thenReturn(1L);
    Mockito.when(passwordLostRequestJpaRepository.findByUserId(userAuth.getId()))
        .thenReturn(Sets.newHashSet(Mockito.mock(PasswordLostRequest.class)));
    final Set<PasswordLostRequest> passwordLostRequests =
        classUnderTest.findByRequestedUsername(username);
    Assert.assertFalse(passwordLostRequests.isEmpty());
    Assert.assertNotNull(passwordLostRequests.iterator().next());
  }

 
  @Test
  public void shouldDeleteLostPasswordRequestsForUser() {
    final String username = "ausername";
    final UserAuthentication userAuth = Mockito.mock(UserAuthentication.class);
    Mockito.when(userAuth.getId()).thenReturn(2L);
    Mockito.when(userAuthRepository.findByUsername(username)).thenReturn(userAuth);
    classUnderTest.deleteLostPasswordRequestsForUser(username);
    Mockito.verify(passwordLostRequestJpaRepository).deleteAllByUserId(userAuth.getId());
  }

}
