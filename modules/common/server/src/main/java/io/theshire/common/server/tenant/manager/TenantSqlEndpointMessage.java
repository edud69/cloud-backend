

package io.theshire.common.server.tenant.manager;

import io.theshire.common.utils.transport.message.TransportMessage;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TenantSqlEndpointMessage extends TransportMessage {

  private static final long serialVersionUID = 9035708785518701752L;

 
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
