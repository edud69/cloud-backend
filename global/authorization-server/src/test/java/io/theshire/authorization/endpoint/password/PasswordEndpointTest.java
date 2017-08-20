

package io.theshire.authorization.endpoint.password;

import static org.junit.Assert.assertEquals;

import io.theshire.authorization.domain.password.PasswordChangeRequest;
import io.theshire.authorization.domain.password.PasswordRestoreRequest;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationNotFoundException;
import io.theshire.authorization.endpoint.password.message.PasswordChangeRequestMessage;
import io.theshire.authorization.endpoint.password.message.PasswordLostRequestMessage;
import io.theshire.authorization.service.password.PasswordChangeInPort;
import io.theshire.authorization.service.password.PasswordLostInPort;
import io.theshire.authorization.service.password.PasswordUpdateService;
import io.theshire.common.domain.exception.DomainException;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RunWith(MockitoJUnitRunner.class)
public class PasswordEndpointTest {

 
  @Mock
  private PasswordUpdateService passwordUpdateService;

 
  @InjectMocks
  private PasswordEndpoint passwordEndpoint;

 
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

 
  @Test
  public void shouldHttpPostForLostPassword() throws UserAuthenticationNotFoundException {
    final PasswordLostRequestMessage request = new PasswordLostRequestMessage();
    request.setUsername("aUsername");

    final ResponseEntity<String> result =
        this.passwordEndpoint.httpPostLostPasswordRequest(request);
    assertEquals(HttpStatus.OK, result.getStatusCode());

    Mockito.verify(this.passwordUpdateService)
        .processRequest(Mockito.argThat(new TypeSafeMatcher<PasswordLostInPort>() {

          @Override
          public void describeTo(Description description) {
            description.appendText("a Inport with request: " + request);
          }

          @Override
          protected boolean matchesSafely(PasswordLostInPort item) {
            return item.getUsername().equals(request.getUsername());
          }

        }), Mockito.any());
  }

 
  @Test
  public void shouldHttpPostLostPasswordRequestWhenPasswordChange() throws DomainException {
    final PasswordChangeRequestMessage req = new PasswordChangeRequestMessage();
    req.setUseLostPasswordToken(true); // should switch automatically in endpoint
    req.setNewPassword("newPassword");
    req.setOldPassword("oldPassword");
    req.setUsername("username");

    final ResponseEntity<String> result =
        this.passwordEndpoint.httpPostForPasswordChangeRequest(req);
    assertEquals(HttpStatus.OK, result.getStatusCode());

    Mockito.verify(this.passwordUpdateService)
        .processRequest(Mockito.argThat(new TypeSafeMatcher<PasswordChangeInPort>() {

          @Override
          public void describeTo(Description description) {
            description.appendText("a Inport with request: " + req);
          }

          @Override
          protected boolean matchesSafely(PasswordChangeInPort item) {
            return item.getRequest() instanceof PasswordChangeRequest
                && item.getRequest().getNewPasswordToSet().equals("newPassword")
                && item.getRequest().getRequestedUsername().equals("username")
                && ((PasswordChangeRequest) item.getRequest()).getPreviousPassword()
                    .equals("oldPassword");
          }

        }), Mockito.any());
  }

  @Test
  public void shouldHttpPostRestorePasswordRequest() throws DomainException {
    final PasswordChangeRequestMessage req = new PasswordChangeRequestMessage();
    req.setUseLostPasswordToken(false); // should switch automatically in endpoint
    req.setNewPassword("newPassword");
    req.setLostPasswordToken("aToken");
    req.setUsername("username");

    final ResponseEntity<String> result = this.passwordEndpoint.httpPostRestorePasswordRequest(req);
    assertEquals(HttpStatus.OK, result.getStatusCode());

    Mockito.verify(this.passwordUpdateService)
        .processRequest(Mockito.argThat(new TypeSafeMatcher<PasswordChangeInPort>() {

          @Override
          public void describeTo(Description description) {
            description.appendText("a Inport with request: " + req);
          }

          @Override
          protected boolean matchesSafely(PasswordChangeInPort item) {
            return item.getRequest() instanceof PasswordRestoreRequest
                && item.getRequest().getNewPasswordToSet().equals("newPassword")
                && item.getRequest().getRequestedUsername().equals("username")
                && ((PasswordRestoreRequest) item.getRequest()).getConfirmationToken()
                    .equals("aToken");
          }

        }), Mockito.any());
  }

}
