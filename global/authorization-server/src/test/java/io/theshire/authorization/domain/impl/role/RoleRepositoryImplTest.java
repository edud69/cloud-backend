

package io.theshire.authorization.domain.impl.role;

import io.theshire.authorization.domain.role.Role;
import io.theshire.authorization.domain.role.RoleLabel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RunWith(MockitoJUnitRunner.class)
public class RoleRepositoryImplTest {

 
  @Mock
  private UserRoleJpaRepository userRoleJpaRepository;

 
  @Mock
  private RoleJpaRepository roleJpaRepository;

 
  @InjectMocks
  private final RoleRepositoryImpl classUnderTest = new RoleRepositoryImpl();

 
  @Test
  public void findByUserIdTest() {
    final Long foundId = 1L;
    final Role expectedRole = new Role(new RoleLabel("A_ROLE"));
    final UserRole foundUserRole = new UserRole(3L, expectedRole);
    final Set<UserRole> foundUserRoles = new HashSet<>();
    foundUserRoles.add(foundUserRole);
    final Long notFoundId = 2L;

    Mockito.when(userRoleJpaRepository.findByUserId(foundId)).thenReturn(foundUserRoles);
    Mockito.when(userRoleJpaRepository.findByUserId(notFoundId)).thenReturn(null);

    final Set<Role> actualFound = classUnderTest.findByUserId(foundId);
    Assert.assertNotNull(actualFound);
    Assert.assertEquals(1, actualFound.size());
    Assert.assertEquals(expectedRole, actualFound.iterator().next());

    final Set<Role> notFound = classUnderTest.findByUserId(notFoundId);
    Assert.assertNotNull(notFound);
    Assert.assertTrue(notFound.isEmpty());
  }

 
  @Test
  public void findByIdTest() {
    final Long existingId = 1L;
    final Role expectedRole = new Role(new RoleLabel("A_ROLE"));
    final Long nonExistingId = 2L;

    Mockito.when(roleJpaRepository.findOne(existingId)).thenReturn(expectedRole);
    Mockito.when(roleJpaRepository.findOne(nonExistingId)).thenReturn(null);

    final Role actualRoleFound = classUnderTest.findById(existingId);
    final Role actualRoleNotFound = classUnderTest.findById(nonExistingId);

    Assert.assertNotNull(actualRoleFound);
    Assert.assertNull(actualRoleNotFound);
    Assert.assertEquals(expectedRole, actualRoleFound);
  }

 
  @Test
  public void saveTest() {
    final Role initRole = new Role(new RoleLabel("A_ROLE"));
    final Role expected = new Role(new RoleLabel("SAVED_ROLE"));

    Mockito.when(roleJpaRepository.save(initRole)).thenReturn(expected);

    final Role actual = classUnderTest.save(initRole);

    Assert.assertNotNull(actual);
    Assert.assertEquals(expected, actual);
  }

 
  @Test
  public void findByRoleNameTest() {
    final RoleLabel roleLabel = new RoleLabel("A_ROLE");
    final Role expectedRole = new Role(roleLabel);
    final RoleLabel nonExistingRoleLabel = new RoleLabel("NON_EXISTING");

    Mockito.when(roleJpaRepository.findByRolename(roleLabel)).thenReturn(expectedRole);
    Mockito.when(roleJpaRepository.findByRolename(nonExistingRoleLabel)).thenReturn(null);

    final Role actualRoleFound = classUnderTest.findByRolename(roleLabel);
    final Role actualRoleNotFound = classUnderTest.findByRolename(nonExistingRoleLabel);

    Assert.assertNotNull(actualRoleFound);
    Assert.assertNull(actualRoleNotFound);
    Assert.assertEquals(expectedRole, actualRoleFound);
  }

 
  @SuppressWarnings("unchecked")
  @Test
  public void saveUserRolesTest() {
    final Long existingUserId = 1L;
    final Set<UserRole> initUserRoles = null;

    final Set<Role> newRoleSet = new HashSet<>();
    final Role role1 = new Role(new RoleLabel("ROLE1"));
    final Role role2 = new Role(new RoleLabel("ROLE2"));
    newRoleSet.add(role1);
    newRoleSet.add(role2);

    final List<UserRole> finalUserRoles = new ArrayList<>();
    finalUserRoles.add(new UserRole(existingUserId, role1));
    finalUserRoles.add(new UserRole(existingUserId, role2));

    Mockito.when(userRoleJpaRepository.findByUserId(existingUserId)).thenReturn(initUserRoles);
    Mockito.when(userRoleJpaRepository.save(Mockito.anyCollection())).thenReturn(finalUserRoles);

    final Set<Role> actualRoles = classUnderTest.saveUserRoles(existingUserId, newRoleSet);

    Mockito.verify(userRoleJpaRepository)
        .save(Mockito.argThat(new SaveRoleMatcher(finalUserRoles)));

    Assert.assertNotNull(actualRoles);
    Assert.assertEquals(2, actualRoles.size());
    Assert.assertTrue(actualRoles.contains(role1));
    Assert.assertTrue(actualRoles.contains(role2));
  }

 
  @SuppressWarnings("unchecked")
  @Test
  public void saveUserRolesWithSyncTest() {
    // case roles need sync
    final Long existingUserId = 1L;
    final Role role1 = new Role(new RoleLabel("ROLE1"));
    final Role role2 = new Role(new RoleLabel("ROLE2"));
    final Role role3 = new Role(new RoleLabel("ROLE3"));
    final Set<UserRole> initUserRoles = new HashSet<>();
    initUserRoles.add(new UserRole(existingUserId, role1));
    initUserRoles.add(new UserRole(existingUserId, role2));
    initUserRoles.add(new UserRole(existingUserId, role3));

    final Set<Role> newRoleSet = new HashSet<>();
    final Role role4 = new Role(new RoleLabel("ROLE4"));
    newRoleSet.add(role1);
    newRoleSet.add(role2);
    newRoleSet.add(role4);

    final List<UserRole> finalUserRoles = new ArrayList<>();
    finalUserRoles.add(new UserRole(existingUserId, role1));
    finalUserRoles.add(new UserRole(existingUserId, role2));
    finalUserRoles.add(new UserRole(existingUserId, role4));

    Mockito.when(userRoleJpaRepository.findByUserId(existingUserId)).thenReturn(initUserRoles);
    Mockito.when(userRoleJpaRepository.save(Mockito.anyCollection())).thenReturn(finalUserRoles);

    final Set<Role> actualRoles = classUnderTest.saveUserRoles(existingUserId, newRoleSet);

    Mockito.verify(userRoleJpaRepository).deleteInBatch(Mockito.argThat(new DeletedRoleMatcher()));
    Mockito.verify(userRoleJpaRepository)
        .save(Mockito.argThat(new SaveRoleMatcher(finalUserRoles)));

    Assert.assertNotNull(actualRoles);
    Assert.assertEquals(3, actualRoles.size());
    Assert.assertTrue(actualRoles.contains(role1));
    Assert.assertTrue(actualRoles.contains(role2));
    Assert.assertTrue(actualRoles.contains(role4));
  }

 
  @SuppressWarnings("rawtypes")
  private class SaveRoleMatcher extends ArgumentMatcher<Collection> {

   
    private final List<UserRole> expected;

   
    public SaveRoleMatcher(final List<UserRole> expected) {
      this.expected = expected;
    }

  
    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object argument) {
      if (argument != null) {
        Set<UserRole> userRoles = (Set<UserRole>) argument;
        if (userRoles.size() == expected.size()) {
          final Set<String> expectedRolenames =
              expected.stream().map(r -> r.getRolename()).collect(Collectors.toSet());
          return userRoles.stream().filter(ur -> expectedRolenames.contains(ur.getRolename()))
              .count() == expected.size();
        }
      }

      return false;
    }

  }

 
  @SuppressWarnings("rawtypes")
  private class DeletedRoleMatcher extends ArgumentMatcher<Set> {

   
    private final Role roleToDelete = new Role(new RoleLabel("ROLE3"));

  
    @Override
    public boolean matches(Object argument) {
      boolean matches = false;
      if (argument != null) {
        @SuppressWarnings("unchecked")
        final Set<UserRole> toDelete = (Set<UserRole>) argument;
        if (toDelete.size() == 1) {
          final UserRole currentRoleToDelete = toDelete.iterator().next();
          if (currentRoleToDelete.getRole().getName().equals(roleToDelete.getName())) {
            matches = true;
          }
        }
      }

      return matches;
    }
  }

}
