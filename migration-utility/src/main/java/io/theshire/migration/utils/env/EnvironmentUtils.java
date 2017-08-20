

package io.theshire.migration.utils.env;

import io.theshire.migration.constants.EnvConstants;


public class EnvironmentUtils {

 
  public static boolean isDevModeEnabled() {
    final String val = System.getProperty(EnvConstants.DEV_MODE_ARG);
    return val != null ? val.equals("true") : false;
  }

}
