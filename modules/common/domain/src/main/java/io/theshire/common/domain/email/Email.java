

package io.theshire.common.domain.email;

import io.theshire.common.domain.DomainObject;

import lombok.Getter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.Transient;


@MappedSuperclass
public class Email extends DomainObject {

 
  private static final long serialVersionUID = -3768324368030737181L;

 
  @Column(name = "subject")

 
  @Getter
  private String subject;

 
  @Embedded
  @AttributeOverride(name = "email", column = @Column(name = "sender"))

 
  @Getter
  private EmailAddress from;

 
  @Transient
  private Set<EmailAddress> to = new HashSet<>();

 
  @Transient
  private Set<EmailAddress> ccs = new HashSet<>();

 
  @Column(name = "recipients")
  private String mappedTo;

 
  @Column(name = "ccs")
  private String mappedCcs;

 
  @Column(name = "body")

 
  @Getter
  protected String body;

 
  @Column(name = "use_html_body")

 
  @Getter
  private boolean useHtmlBody;

 
  @Column(name = "sent_time")
  private LocalDateTime sentTime;

 
  public boolean isSent() {
    return sentTime != null;
  }

 
  public void markNowAsSent() {
    if (sentTime != null) {
      throw new IllegalStateException("Message was already marked as sent.");
    }
    sentTime = LocalDateTime.now(Clock.systemUTC());
  }

 
  public Set<EmailAddress> getTo() {
    return Collections.unmodifiableSet(to);
  }

 
  public Set<EmailAddress> getCcs() {
    return Collections.unmodifiableSet(ccs);
  }

 
  @PostLoad
  protected void loadToAndCcs() {
    ccs = toEmails(mappedCcs);
    to = toEmails(mappedTo);
  }

 
  private Set<EmailAddress> toEmails(final String mails) {
    if (mails == null) {
      return new HashSet<>();
    }
    final Set<EmailAddress> addrs = new HashSet<>();
    final String[] mailFragments = mails.split(";");
    for (final String mail : mailFragments) {
      if (mail.trim().isEmpty()) {
        continue;
      }
      addrs.add(new EmailAddress(mail));
    }
    return addrs;
  }

 
  private void loadTo() {
    if (to != null) {
      final StringBuilder sb = new StringBuilder();
      to.forEach(emailAddr -> sb.append(emailAddr.getEmail()).append(";"));
      mappedTo = sb.toString();
    }
  }

 
  private void loadCcs() {
    if (ccs != null) {
      final StringBuilder sb = new StringBuilder();
      ccs.forEach(emailAddr -> sb.append(emailAddr.getEmail()).append(";"));
      mappedCcs = sb.toString();
    }
  }

 
  public Email(String subject, EmailAddress from, Set<EmailAddress> to, Set<EmailAddress> ccs,
      String body, boolean useHtmlBody) {
    super();
    this.subject = subject;
    this.from = from;
    this.to = to == null ? this.to : to;
    this.ccs = ccs == null ? this.ccs : ccs;
    this.body = body;
    this.useHtmlBody = useHtmlBody;
    loadTo();
    loadCcs();
  }

 
  protected Email() {

  }
}
