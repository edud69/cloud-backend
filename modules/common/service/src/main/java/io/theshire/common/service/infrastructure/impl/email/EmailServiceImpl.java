

package io.theshire.common.service.infrastructure.impl.email;

import io.theshire.common.domain.email.Email;
import io.theshire.common.domain.email.EmailAddress;
import io.theshire.common.service.infrastructure.email.EmailService;

import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


@Service
public class EmailServiceImpl implements EmailService {

 
  @Autowired
  private JavaMailSender javaMailSender;


  @Override
  public <T extends Email> T  send(final T email) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();

      // use the true flag to indicate you need a multipart message
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setTo(convertToStringMails(email.getTo()));

      // use the true flag to indicate the text included is HTML
      helper.setText(email.getBody(), email.isUseHtmlBody());

      final String ccs = convertToStringMails(email.getCcs());
      if (ccs != null) {
        helper.setCc(ccs);
      }

      helper.setSubject(email.getSubject());
      helper.setFrom(email.getFrom().getEmail());

      // TODO attachments ??? (call or store in document-service)

      javaMailSender.send(message);
      email.markNowAsSent();
      return email;
    } catch (final MessagingException exception) {
      throw new EmailException(exception.getMessage(), exception);
    }
  }

 
  private String convertToStringMails(final Set<EmailAddress> emails) {
    if (emails.isEmpty()) {
      return null;
    }

    final StringBuilder sb = new StringBuilder();
    final int total = emails.size();
    final MutableInt count = new MutableInt(0);

    emails.forEach(mail -> {
      sb.append(mail.getEmail()).append(count.getValue() < total - 1 ? ";" : "");
      count.increment();
    });

    return sb.toString();
  }

}
