

package io.theshire.common.domain.token;

import io.theshire.common.domain.ValueObject;

import lombok.Getter;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class RandomConfirmationToken extends ValueObject {

 
  private static final long serialVersionUID = -5532697374484173648L;

 
  @Column(name = "confirmation_token")
 
  @Getter
  private String token;

 
  public boolean isSame(final String token) {
    return this.token.equals(token);
  }

 
  public RandomConfirmationToken() {
    this.token = UUID.randomUUID().toString().replace("-", "");
  }
}
