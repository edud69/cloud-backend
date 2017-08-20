

package io.theshire.authorization.domain.impl.user.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;

import io.theshire.authorization.domain.role.Role;
import io.theshire.authorization.domain.role.RoleLabel;
import io.theshire.authorization.domain.role.RoleRepository;
import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationRoleInitializer;
import io.theshire.common.utils.transaction.TransactionUtils;

import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class UserAuthenticationRepositoryImplTest {

 
  @Mock
  private RoleRepository roleRepository;

 
  @Mock
  private TransactionUtils transactionUtils;

 
  @Mock
  private UserAuthenticationJpaSingleTenantRepository userAuthenticationJpaRepository;

 
  @Mock
  private UserAuthenticationRoleInitializer userAuthenticationRoleInitializer;

 
  @InjectMocks
  private UserAuthenticationRepositoryImpl classUnderTest;

 
  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

 
  @Test
  public void shouldFindByIdAndFetchRoles() {
    final Long userId = 3L;
    final UserAuthentication userAuth = mock(UserAuthentication.class);
    when(userAuthenticationJpaRepository.findOne(userId)).thenReturn(userAuth);
    final UserAuthentication userAuthWithFetchedRoles = mock(UserAuthentication.class);
    when(userAuthWithFetchedRoles.getRoles())
        .thenReturn(Sets.newHashSet(new Role(new RoleLabel("HelloRole"))));
    when(userAuthenticationRoleInitializer.fetchTenantCtxRoles(userAuth, null))
        .thenReturn(userAuthWithFetchedRoles);
    final UserAuthentication result = classUnderTest.findById(userId);
    assertEquals("HelloRole", result.getRoles().iterator().next().getName());
  }

 
  @Test
  public void shouldFindByUsernameAndFetchRoles() {
    final String username = "aUsername";
    final UserAuthentication userAuth = mock(UserAuthentication.class);
    when(userAuthenticationJpaRepository.findByUsername(username)).thenReturn(userAuth);
    final UserAuthentication userAuthWithFetchedRoles = mock(UserAuthentication.class);
    when(userAuthWithFetchedRoles.getRoles())
        .thenReturn(Sets.newHashSet(new Role(new RoleLabel("HelloRole"))));
    when(userAuthenticationRoleInitializer.fetchTenantCtxRoles(userAuth, null))
        .thenReturn(userAuthWithFetchedRoles);
    final UserAuthentication result = classUnderTest.findByUsername(username);
    assertEquals("HelloRole", result.getRoles().iterator().next().getName());
  }

 
  @Test
  public void shouldRollbackWithExistingUserOnSaveError() {
    final UserAuthentication toBeSaved = Mockito.mock(UserAuthentication.class);
    final UserAuthentication existingUser = Mockito.mock(UserAuthentication.class);
    when(userAuthenticationJpaRepository.findOne(any(Long.class))).thenReturn(existingUser);
    when(userAuthenticationJpaRepository.save(toBeSaved)).thenReturn(toBeSaved);
    Mockito.when(roleRepository.saveUserRoles(Mockito.anyLong(), Mockito.any()))
        .thenThrow(new RuntimeException());

    final MutableObject<Runnable> rollbackRunnable = new MutableObject<>();
    doAnswer(new Answer<Void>() {

      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        rollbackRunnable.setValue((Runnable) invocation.getArguments()[0]);
        return null;
      }
    }).when(transactionUtils).runAfterRollback(Mockito.any());

    try {
      classUnderTest.save(toBeSaved);
      fail("Should have thrown...");
    } catch (final Exception exc) {
      // simulate the transaction rollback call
      rollbackRunnable.getValue().run();
      Mockito.verify(userAuthenticationJpaRepository).save(existingUser);
    }
  }

 
  @Test
  public void shouldRollbackAndDeleteUserIfNoneWasExistingOnSaveError() {
    final UserAuthentication toBeSaved = Mockito.mock(UserAuthentication.class);
    final UserAuthentication savedUserAuth = Mockito.mock(UserAuthentication.class);
    final UserAuthentication existingUser = null;

    when(savedUserAuth.getId()).thenReturn(3L);
    when(userAuthenticationJpaRepository.findOne(any(Long.class))).thenReturn(existingUser);
    when(userAuthenticationJpaRepository.save(toBeSaved)).thenReturn(savedUserAuth);

    final MutableObject<Runnable> rollbackRunnable = new MutableObject<>();
    doAnswer(new Answer<Void>() {

      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        rollbackRunnable.setValue((Runnable) invocation.getArguments()[0]);
        return null;
      }
    }).when(transactionUtils).runAfterRollback(Mockito.any());

    when(roleRepository.saveUserRoles(savedUserAuth.getId(), toBeSaved.getRoles()))
        .thenReturn(Sets.newHashSet(new Role(new RoleLabel("aRole"))));

    when(userAuthenticationRoleInitializer.fetchTenantCtxRoles(any(), any()))
        .thenReturn(savedUserAuth);

    final UserAuthentication saved = classUnderTest.save(toBeSaved);
    assertEquals(savedUserAuth, saved);

    // crashes after...
    // simulate the transaction rollback call
    rollbackRunnable.getValue().run();
    verify(userAuthenticationJpaRepository).delete(savedUserAuth.getId());
  }

}
