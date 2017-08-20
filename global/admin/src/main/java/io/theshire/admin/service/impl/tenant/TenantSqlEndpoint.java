

package io.theshire.admin.service.impl.tenant;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Getter;
import lombok.Setter;


@Getter


@Setter
public class TenantSqlEndpoint extends TransportMessage {

 
  private static final long serialVersionUID = 8580538090014503572L;

 
  private String url;

 
  private String password;

 
  private String username;

 
  private String driveClass;

 
  private int maxActive;

 
  private int maxWait;

 
  private int initialSize;

 
  private int maxIdle;

 
  private int minIdle;

}
