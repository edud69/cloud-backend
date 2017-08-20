

package io.theshire.admin.service.impl.tenant;

import io.theshire.admin.domain.MicroService;
import io.theshire.admin.domain.MicroServiceSqlDbConfig;
import io.theshire.admin.service.microservices.MicroServicesListingService;
import io.theshire.admin.service.tenant.TenantAddService;
import io.theshire.common.utils.tenant.TenantIdValidator;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
class TenantAddServiceImpl implements TenantAddService {

 
  private static final String MQ_ROUTE_PREFIX = "tenant.state.change.";

 
  @Autowired
  private MicroServicesListingService microServicesListingService;

 
  @Autowired
  private RabbitTemplate messagingTemplate;


  @Override
  public void addNewTenant(String tenantId) {
    TenantIdValidator.validate(tenantId);
    microServicesListingService.listMicroServices().forEach(ms -> initializeTenant(ms, tenantId));
  }


  @Override
  public void addNewTenant(String tenantId, String serviceName) {
    log.info("Adding tenant {} to serviceName {}.", tenantId, serviceName);
    TenantIdValidator.validate(tenantId);
    final Optional<MicroService> ms = microServicesListingService.getMicroService(serviceName);
    if (ms.isPresent()) {
      initializeTenant(ms.get(), tenantId);
    }
  }

 
  private void initializeTenant(final MicroService ms, final String tenantId) {
    TenantSqlEndpoint sqlEndpoint = null;

    if (ms.hasSqlActive()) {
      sqlEndpoint = new TenantSqlEndpoint();
      final MicroServiceSqlDbConfig mainNodeDbConfig = ms.getDbConfig();
      sqlEndpoint.setDriveClass(mainNodeDbConfig.getDriverClassName());
      sqlEndpoint.setUsername(mainNodeDbConfig.getUsername());
      sqlEndpoint.setPassword(mainNodeDbConfig.getPassword());
      sqlEndpoint.setUrl(mainNodeDbConfig.getUrl());
      sqlEndpoint.setMaxActive(10);
      sqlEndpoint.setMaxWait(15);
      sqlEndpoint.setInitialSize(5);
      sqlEndpoint.setMaxIdle(7);
      sqlEndpoint.setMinIdle(5);
    }

    notifyMicroService(ms, tenantId, sqlEndpoint);
  }

 
  private void notifyMicroService(final MicroService ms, final String tenantId,
      final TenantSqlEndpoint sqlEndpoint) {
    final TenantAddMessage msg = new TenantAddMessage();
    msg.setTenantId(tenantId);
    msg.setSqlEndpoint(sqlEndpoint);
    messagingTemplate.convertAndSend(MQ_ROUTE_PREFIX + ms.getServiceName(), "#", msg);
  }

}
