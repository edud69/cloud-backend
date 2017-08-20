

package io.theshire.account.endpoint.message;

import io.theshire.common.domain.type.Gender;
import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@Data
@EqualsAndHashCode(callSuper = true)
public class FullAccountInfoMsg extends TransportMessage {

 
  private static final long serialVersionUID = -1127731433334979292L;

 
  private Long userId;

 
  private String firstName;

 
  private String lastName;

 
  private Gender gender;

 
  private LocalDate birthday;

 
  private String avatarUrl;

}
