

package io.theshire.authorization.domain.password;

import io.theshire.authorization.domain.impl.user.authentication.UserAuthenticationInvalidPasswordFormatException;
import io.theshire.authorization.domain.password.PasswordChangeException.PasswordChangeExceptionBuilder;
import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.authorization.domain.user.authentication.UserAuthenticationRepository;
import io.theshire.common.utils.security.authentication.AuthenticationContext;
import io.theshire.common.utils.security.encryptor.CredentialsEncryptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Set;


@Component
public class PasswordUpdater {

 
  @Autowired
  private UserAuthenticationRepository userAuthRepository;

 
  @Autowired
  private PasswordLostRequestRepository passwordLostRequestRepository;

 
  private final CredentialsEncryptor credentialsEncryptor = new CredentialsEncryptor();

 
  public void process(final PasswordUpdateRequest request)
      throws PasswordChangeException, UserAuthenticationInvalidPasswordFormatException {
    Assert.notNull(request, "request cannot be null.");

    if (!request.isLostPasswordRestoreRequest()
        && !AuthenticationContext.get().getUsername().equals(request.getRequestedUsername())) {
      throw new PasswordChangeExceptionBuilder()
          .asChangingAnotherUserPasswordIsForbidden("Changing another user password is forbidden.")
          .build();
    }

    if (request.isLostPasswordRestoreRequest()) {
      processAsLostPassword((PasswordRestoreRequest) request);
    } else {
      processAsPasswordChange((PasswordChangeRequest) request);
    }
  }

 
  private void processAsLostPassword(final PasswordRestoreRequest request)
      throws PasswordChangeException, UserAuthenticationInvalidPasswordFormatException {
    final String lostPasswordToken = request.getConfirmationToken();
    final Set<PasswordLostRequest> pLostRequests =
        passwordLostRequestRepository.findByRequestedUsername(request.getRequestedUsername());

    // get existing confirmation token
    final Optional<PasswordLostRequest> match = pLostRequests.stream().filter(
        lostReq -> lostReq.getConfirmationToken().isSame(lostPasswordToken) && !lostReq.isExpired())
        .findAny();

    if (!match.isPresent()) {
      throw new PasswordChangeExceptionBuilder()
          .asNoLostPasswordRequestFound("No lost password request that are not expired or "
              + "matches given token was found for username=" + request.getRequestedUsername()
              + ".")
          .build();
    }

    // delete all expired confirmation token from lost passwords
    passwordLostRequestRepository.deleteLostPasswordRequestsForUser(request.getRequestedUsername());

    updatePassword(request.getNewPasswordToSet(),
        userAuthRepository.findByUsername(request.getRequestedUsername()));
  }

 
  private void processAsPasswordChange(final PasswordChangeRequest request)
      throws PasswordChangeException, UserAuthenticationInvalidPasswordFormatException {
    final String passwordToConfirmWithExisting = request.getPreviousPassword();
    final UserAuthentication userAuth =
        userAuthRepository.findByUsername(request.getRequestedUsername());

    // compare password
    if (!credentialsEncryptor.matches(passwordToConfirmWithExisting, userAuth.getPassword())) {
      throw new PasswordChangeExceptionBuilder().asPreviousPasswordCheckMismatch(
          "Cannot change password due to a mismatch with previous password for user = "
              + userAuth.getUsername() + ".")
          .build();
    }

    updatePassword(request.getNewPasswordToSet(), userAuth);
  }

 
  private void updatePassword(final String newPassword, final UserAuthentication userAuth)
      throws UserAuthenticationInvalidPasswordFormatException {
    userAuth.changePassword(newPassword);
    userAuthRepository.save(userAuth);
  }

}
