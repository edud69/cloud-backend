

package io.theshire.common.server.tenant.manager;

import io.theshire.common.service.infrastructure.impersonification.ImpersonificationService;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;



@Slf4j
@Component
@EnableBinding(TenantStateProcessor.class)
class TenantStateListener {

 
  @Autowired
  private TenantManager tenantManager;

 
  @Autowired
  private ImpersonificationService impersonificationService;

 
  @Value("${app.cloud.auth.specialUser.user.tenantManager.username}")
  private String systemTenantManagerUsername;

 
  @Value("${app.cloud.auth.specialUser.user.tenantManager.password}")
  private String systemTenantManagerPassword;

 
  @StreamListener(TenantStateProcessor.INPUT)
  public void receive(final TenantStateChangeMessage tenantStateChangeMessage) throws Exception {
    log.debug("Tenant state changed request received, {}.", tenantStateChangeMessage);

    final MutableObject<Exception> exception = new MutableObject<>();

    impersonificationService.runWithImpersonated(() -> {
      try {
        tenantManager.processQuery(tenantStateChangeMessage);
      } catch (Exception exc) {
        exception.setValue(exc);
      }
    }, systemTenantManagerUsername, systemTenantManagerPassword);

    if (exception.getValue() != null) {
      throw exception.getValue();
    }
  }

}
