

package io.theshire.authorization.domain.user.subscription;

import io.theshire.common.domain.email.Email;
import io.theshire.common.domain.email.EmailAddress;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "users_subscriptions_invitations")
public class UserSubscriptionInvitation extends Email {

 
  private static final long serialVersionUID = -4485086087895443417L;

 
  private static final String EMAIL_BODY_CONFIRMTOKEN_PLACEHOLDER_KEY = "${CONFIRMATION_TOKEN}";

 
  private static final String EMAIL_BODY_USERMAIL_PLACEHOLDER_KEY = "${USER_EMAIL}";

 
  private static final String EMAIL_BODY_WEBSITE_ROOT_URL_KEY = "${WEBSITE_ROOT_URL}";

 
  @NotNull
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
  @JoinColumn(name = "subscription_id", referencedColumnName = "id")
  protected UserSubscription userSubscription;

 
  protected void injectTokenValueInBody(final String tokenValue) {
    this.body = this.body.replace(EMAIL_BODY_CONFIRMTOKEN_PLACEHOLDER_KEY, tokenValue);
  }

 
  public UserSubscriptionInvitation(String subject, EmailAddress from, Set<EmailAddress> to,
      Set<EmailAddress> ccs, String body, String websiteUrlRoot, boolean useHtmlBody) {
    super(subject, from, to, ccs, body, useHtmlBody);
    String urlParam;
    try {
      urlParam = URLEncoder.encode(to.stream().findFirst().get().getEmail(), "UTF-8");
      this.body = this.body.replace(EMAIL_BODY_USERMAIL_PLACEHOLDER_KEY, urlParam)
          .replace(EMAIL_BODY_WEBSITE_ROOT_URL_KEY, websiteUrlRoot);
    } catch (UnsupportedEncodingException exc) {
      throw new IllegalArgumentException("Could not encode URL paramter.", exc);
    }
  }

 
  protected UserSubscriptionInvitation() {

  }

}
