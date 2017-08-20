

package io.theshire.admin.service.impl.tenant;

import io.theshire.admin.domain.MicroService;
import io.theshire.admin.service.microservices.MicroServicesListingService;
import io.theshire.admin.service.tenant.TenantRemoveService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
class TenantRemoveServiceImpl implements TenantRemoveService {

 
  private static final String MQ_ROUTE_PREFIX = "tenant.state.change.";

 
  @Autowired
  private MicroServicesListingService microServicesListingService;

 
  @Autowired
  private RabbitTemplate messagingTemplate;


  @Override
  public void removeTenant(String tenantId) {
    log.info("Removing tenantId: {}.", tenantId);
    microServicesListingService.listMicroServices().forEach(ms -> notifyMicroService(ms, tenantId));
  }

 
  private void notifyMicroService(final MicroService ms, final String tenantId) {
    final TenantRemoveMessage msg = new TenantRemoveMessage();
    msg.setTenantId(tenantId);
    messagingTemplate.convertAndSend(MQ_ROUTE_PREFIX + ms.getServiceName(), "#", msg);
  }

}
