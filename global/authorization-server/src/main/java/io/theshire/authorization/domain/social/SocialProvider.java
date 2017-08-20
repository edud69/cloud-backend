

package io.theshire.authorization.domain.social;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;




@AllArgsConstructor
public enum SocialProvider {

 
  FACEBOOK("facebook"),

 
  GOOGLE("google");

 
  @Getter
  private String id;

 
  public static SocialProvider fromId(final String value) {
    return Arrays.stream(SocialProvider.values()).filter(e -> e.getId().equals(value)).findFirst()
        .orElseThrow(() -> new IllegalStateException(
            String.format("Unsupported social provider %s.", value)));
  }

}
