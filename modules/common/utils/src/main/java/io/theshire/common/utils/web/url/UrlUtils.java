

package io.theshire.common.utils.web.url;

import java.util.Arrays;


public class UrlUtils {

 
  public static String extractParameterValueFromQueryUri(final String query,
      final String paramName) {
    return Arrays.asList(query.split("&")).stream()
        .filter(paramValueEntry -> paramName.equals(paramValueEntry.split("=")[0]))
        .map(entry -> entry.split("=")[1]).findFirst().orElse(null);
  }

}
