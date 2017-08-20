

package io.theshire.authorization.domain.impl.role;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import io.theshire.authorization.domain.role.Role;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UserRoleTest {

 
  @Mock
  private Role role;

 
  @InjectMocks
  private UserRole classUnderTest;

 
  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

 
  @Test
  public void shouldConstruct() {
    final Role role = Mockito.mock(Role.class);
    final UserRole userRole = new UserRole(2L, role);
    assertEquals(2L, userRole.getUserId().longValue());
  }

 
  @Test
  public void shouldGetRolename() {
    when(role.getName()).thenReturn("aRolename");
    assertEquals(role.getName(), classUnderTest.getRolename());
  }

}
