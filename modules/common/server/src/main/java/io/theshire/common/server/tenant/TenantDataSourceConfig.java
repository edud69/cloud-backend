

package io.theshire.common.server.tenant;

import lombok.Builder;
import lombok.Getter;


@Getter


@Builder
public class TenantDataSourceConfig {

 
  private String tenantIdentifier;

 
  private String url;

 
  private String password;

 
  private String username;

 
  private String driveClass;

 
  private int maxActive;

 
  private int maxWait;

 
  private int initialSize;

 
  private int maxIdle;

 
  private int minIdle;

 
  private TenantDataSourceConfig(String tenantIdentifier, String url, String password,
      String username, String driveClass, int maxActive, int maxWait, int initialSize, int maxIdle,
      int minIdle) {
    super();
    this.tenantIdentifier = tenantIdentifier;
    this.url = url;
    this.password = password;
    this.username = username;
    this.driveClass = driveClass;
    this.maxActive = maxActive;
    this.maxWait = maxWait;
    this.initialSize = initialSize;
    this.maxIdle = maxIdle;
    this.minIdle = minIdle;
  }

}
