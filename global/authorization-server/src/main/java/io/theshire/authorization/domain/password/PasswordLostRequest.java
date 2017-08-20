

package io.theshire.authorization.domain.password;

import io.theshire.authorization.domain.user.authentication.UserAuthentication;
import io.theshire.common.domain.DomainObject;
import io.theshire.common.domain.token.RandomConfirmationToken;
import io.theshire.common.server.tenant.TenantDatabaseSchema;

import lombok.Getter;

import org.springframework.util.Assert;

import java.time.Clock;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "lost_password_requests", schema = TenantDatabaseSchema.SINGLE_SIGN_ON_SCHEMA)
public class PasswordLostRequest extends DomainObject {

 
  private static final long serialVersionUID = 7456786725689397661L;

 
  private static final int REQUEST_VALIDITY_IN_HOURS = 24;

 
  @NotNull
  @ManyToOne
  @JoinColumn(name = "requested_for")
  private UserAuthentication requestedFor;

 
  @Getter
  @Column(name = "request_time")
  private LocalDateTime requestTime;

 
  @NotNull
  @Embedded
  @Getter
  private RandomConfirmationToken confirmationToken;

 
  public boolean isExpired() {
    return !LocalDateTime.now(Clock.systemUTC())
        .isBefore(requestTime.plusHours(REQUEST_VALIDITY_IN_HOURS));
  }

 
  public String getRequestedFor() {
    return requestedFor.getUsername();
  }

 
  public PasswordLostRequest(final UserAuthentication userAuth) {
    this();
    Assert.notNull(userAuth, "userAuth cannot be null.");
    requestedFor = userAuth;
  }

 
  protected PasswordLostRequest() {
    super();
    requestTime = LocalDateTime.now();
    confirmationToken = new RandomConfirmationToken();
  }

}
