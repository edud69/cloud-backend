

package io.theshire.common.server.tenant.manager;

import io.theshire.common.server.tenant.manager.TenantStateChangeMessage.TenantState;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;




@Slf4j
@Component
class TenantManagerImpl implements TenantManager {

 
  @Autowired
  private TenantStateProcessor tenantStateProcessor;

 
  @Autowired
  private TenantInitializer tenantInitializer;

 
  @Autowired
  private TenantDestroyer tenantDestroyer;


  @Override
  public void processQuery(TenantStateChangeMessage tenantStateChangeMessage) throws Exception {
    log.info("Tenant state change message query received {}.", tenantStateChangeMessage);

    final String tenantId = tenantStateChangeMessage.getTenantId();

    switch (tenantStateChangeMessage.getTenantState()) {
      case CREATION_PENDING :
        tenantInitializer.initialize(tenantId, tenantStateChangeMessage.getSqlEndpoint());
        tenantStateChangeMessage.setTenantState(TenantState.CREATED);
        tenantStateProcessor.output()
            .send(MessageBuilder.withPayload(tenantStateChangeMessage).build());
        break;
      case CREATED :
        log.debug("Tenant {} was created.", tenantId);
        break;
      case REMOVAL_PENDING :
        tenantDestroyer.destroy(tenantId);
        tenantStateChangeMessage.setTenantState(TenantState.REMOVED);
        tenantStateProcessor.output()
            .send(MessageBuilder.withPayload(tenantStateChangeMessage).build());
        break;
      case REMOVED :
        log.debug("Tenant {} was removed.", tenantId);
        break;
      default :
        throw new IllegalArgumentException("Invalid tenant state: "
            + tenantStateChangeMessage.getTenantState() + " for tenantId : " + tenantId + ".");
    }

  }

}
