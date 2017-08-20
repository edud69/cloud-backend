

package io.theshire.authorization.domain.user.subscription;

import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionActivationException;
import io.theshire.authorization.domain.impl.user.subscription.UserSubscriptionActivationException.UserSubscriptionActivationExceptionBuilder;
import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.common.domain.DomainObject;
import io.theshire.common.domain.token.RandomConfirmationToken;

import lombok.Getter;

import java.time.Clock;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "users_subscriptions")
public class UserSubscription extends DomainObject {

 
  private static final long serialVersionUID = 8473540944790582604L;

 
  @Getter
  @Column(name = "request_time")
  private LocalDateTime requestTime;

 
  @Getter
  @NotNull
  @OneToOne(mappedBy = "userSubscription", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private UserSubscriptionInvitation subscriptionInvitation;

 
  @Getter
  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private UserSubscriptionStatus userSubscriptionStatus;

 
  @Getter
  @NotNull
  @Column(name = "user_id")
  private Long userId;

 
  @NotNull
  @Embedded
  @Getter
  private RandomConfirmationToken confirmationToken;

 
  public UserSubscription(final UserSubscriptionInvitation invitation,
      final UserAuthentication userAuth) {
    this.subscriptionInvitation = invitation;
    this.requestTime = LocalDateTime.now(Clock.systemUTC());
    this.userId = userAuth.getId();
    this.userSubscriptionStatus = UserSubscriptionStatus.PENDING;
    this.confirmationToken = new RandomConfirmationToken();
    this.subscriptionInvitation.injectTokenValueInBody(this.confirmationToken.getToken());
    this.subscriptionInvitation.userSubscription = this;
  }

 
  public boolean isReadyForActivation() {
    return UserSubscriptionStatus.PENDING.equals(userSubscriptionStatus);
  }

 
  public void activate(final String confirmationToken) throws UserSubscriptionActivationException {
    if (!isReadyForActivation()) {
      throw new UserSubscriptionActivationExceptionBuilder()
          .asStateNotReadyForActivation("UserSubscription is not in a pre-activation valid state.")
          .build();
    }
    if (this.confirmationToken == null) {
      throw new UserSubscriptionActivationExceptionBuilder()
          .asNoConfirmationTokenFound("UserSubscription needs a confirmation token.").build();
    }
    if (!this.confirmationToken.isSame(confirmationToken)) {
      throw new UserSubscriptionActivationExceptionBuilder().asInvalidConfirmationToken(
          "UserSubscription confirmation does not match the given confirmation token.").build();
    }
    this.userSubscriptionStatus = UserSubscriptionStatus.ACTIVE;
  }

 
  protected UserSubscription() {

  }
}