

package io.theshire.authorization.endpoint.web.login;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data


@EqualsAndHashCode(callSuper = true)
public class OAuth2TokenMessage extends TransportMessage {

 
  private static final long serialVersionUID = 7404200707750128268L;

 
  private String refreshToken;

 
  private String accessToken;

}
