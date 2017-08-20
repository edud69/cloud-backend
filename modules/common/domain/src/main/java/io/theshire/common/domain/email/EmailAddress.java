

package io.theshire.common.domain.email;

import io.theshire.common.domain.ValueObject;

import lombok.Getter;

import org.apache.commons.validator.routines.EmailValidator;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class EmailAddress extends ValueObject {

 
  private static final long serialVersionUID = -1277381030232028200L;

 
  @Getter
  @Column(name = "email")
  private String email;

 
  public EmailAddress(final String email) {
    this.email = email;
    if (!EmailValidator.getInstance().isValid(email)) {
      throw new IllegalArgumentException("Email is not valid, email=" + email + ".");
    }
  }

 
  protected EmailAddress() {

  }

}
