

package io.theshire.common.domain.email;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Set;


public class EmailTest {

 
  public class EmailTester extends Email {

   
    private static final long serialVersionUID = -6647645284521779888L;

   
    public EmailTester(String subject, EmailAddress from, Set<EmailAddress> to,
        Set<EmailAddress> ccs, String body, boolean useHtmlBody) {
      super(subject, from, to, ccs, body, useHtmlBody);
    }

   
    public void forceMapping() {
      super.loadToAndCcs();
    }

  }

 
  @Test
  public void shouldMapCorrectlyTos() {
    final Set<EmailAddress> to = new HashSet<>();
    to.add(new EmailAddress("test@test.com"));
    to.add(new EmailAddress("test2@test.com"));
    final EmailTester mail = new EmailTester(null, null, to, null, null, false);
    final String mappedTo = (String) ReflectionTestUtils.getField(mail, "mappedTo");
    Assert.assertEquals(mappedTo, "test@test.com;test2@test.com;");

    final EmailTester mailTwo = new EmailTester(null, null, null, null, null, false);
    ReflectionTestUtils.setField(mailTwo, "mappedTo", "test@test.com;test2@test.com;");
    mailTwo.forceMapping();
    Assert.assertEquals(mailTwo.getTo().size(), 2);
    Assert.assertEquals(mailTwo.getTo(), mail.getTo());
  }

 
  @Test
  public void shouldMapCorrectlyCcs() {
    final Set<EmailAddress> ccs = new HashSet<>();
    ccs.add(new EmailAddress("test@test.com"));
    ccs.add(new EmailAddress("test2@test.com"));
    final EmailTester mail = new EmailTester(null, null, null, ccs, null, false);
    final String mappedCcs = (String) ReflectionTestUtils.getField(mail, "mappedCcs");
    Assert.assertEquals(mappedCcs, "test@test.com;test2@test.com;");

    final EmailTester mailTwo = new EmailTester(null, null, null, null, null, false);
    ReflectionTestUtils.setField(mailTwo, "mappedCcs", "test@test.com;test2@test.com;");
    mailTwo.forceMapping();
    Assert.assertEquals(mailTwo.getCcs().size(), 2);
    Assert.assertEquals(mailTwo.getCcs(), mail.getCcs());
  }

 
  @Test
  public void shouldNotBeenSent() {
    final Email mail = new Email(null, null, null, null, null, false);
    Assert.assertFalse(mail.isSent());
  }

 
  @Test
  public void shouldHaveASendTime() {
    final Email mail = new Email(null, null, null, null, null, false);
    mail.markNowAsSent();
    Assert.assertTrue(mail.isSent());
  }

 
  @Test(expected = IllegalStateException.class)
  public void shouldThrowWhenSentTwice() {
    final Email mail = new Email(null, null, null, null, null, false);
    mail.markNowAsSent();
    mail.markNowAsSent();
  }

 
  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowWhenModifyingCcs() {
    final Set<EmailAddress> ccs = new HashSet<>();
    ccs.add(new EmailAddress("test@test.com"));
    final Email mail = new Email(null, null, null, ccs, null, false);
    mail.getCcs().add(new EmailAddress("test2@test.com"));
  }

 
  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowWhenModifyingTo() {
    final Set<EmailAddress> to = new HashSet<>();
    to.add(new EmailAddress("test@test.com"));
    final Email mail = new Email(null, null, to, null, null, false);
    mail.getTo().add(new EmailAddress("test2@test.com"));
  }

 
  @Test
  public void shouldMatchUseHtml() {
    final Email mail = new Email(null, null, null, null, null, false);
    Assert.assertFalse(mail.isUseHtmlBody());
    final Email mailHtml = new Email(null, null, null, null, null, true);
    Assert.assertTrue(mailHtml.isUseHtmlBody());
  }

}
