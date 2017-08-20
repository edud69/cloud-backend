

package io.theshire.admin.service.impl.microservices;

import io.theshire.admin.domain.MicroService;
import io.theshire.admin.domain.MicroServiceSqlDbConfig;
import io.theshire.admin.service.microservices.MicroServicesListingService;
import io.theshire.admin.service.microservices.MicroServicesSqlConfigService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
class MicroServicesListingServiceImpl implements MicroServicesListingService {

 
  @Autowired
  private DiscoveryClient discoveryClient;

 
  @Autowired
  private MicroServicesSqlConfigService microServicesSqlConfigService;


  @Override
  public Set<MicroService> listMicroServices() {
    final Set<String> serviceNames = getMicroServices();
    return serviceNames.stream().map(sName -> process(sName)).filter(e -> e != null)
        .collect(Collectors.toSet());
  }

 
  private Set<String> getMicroServices() {
    return discoveryClient.getServices().stream()
        .filter(e -> e.endsWith("-service") && !e.equals("config-service"))
        .collect(Collectors.toSet());
  }

 
  private MicroService process(final String serviceName) {
    final MicroServiceSqlDbConfig dbConfig = getMicroServiceSqlDbConfig(serviceName);
    return dbConfig == null ? new MicroService(serviceName)
        : new MicroService(serviceName, dbConfig);
  }

 
  private MicroServiceSqlDbConfig getMicroServiceSqlDbConfig(final String serviceName) {
    return microServicesSqlConfigService.retrieveSqlConfig(serviceName);
  }


  @Override
  public Optional<MicroService> getMicroService(final String serviceName) {
    return getMicroServices().stream().filter(e -> e.equals(serviceName))
        .map(sName -> process(sName)).filter(e -> e != null).findAny();
  }

}
