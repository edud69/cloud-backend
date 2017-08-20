

package io.theshire.common.server.tenant.manager;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;


public interface TenantStateProcessor {

 
  String INPUT = "tenantStateChangeInput";

 
  String OUTPUT = "tenantStateChangeOutput";

 
  @Input(TenantStateProcessor.INPUT)
  SubscribableChannel input();

 
  @Output(TenantStateProcessor.OUTPUT)
  MessageChannel output();

}
