
package io.theshire.common.utils.tenant;

import java.util.regex.Pattern;

public class TenantIdValidator {

  private static Pattern validTenantId = Pattern.compile("^[a-zA-Z0-9]*$");

 
  public static void validate(final String tenantId) {
    if (!validTenantId.matcher(tenantId).matches()) {
      throw new IllegalArgumentException(
          "The tenant ID is not valid. Use aplhanumeric characters only.");
    }
  }
}
