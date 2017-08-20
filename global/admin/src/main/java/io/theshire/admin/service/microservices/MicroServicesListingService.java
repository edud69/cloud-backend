

package io.theshire.admin.service.microservices;

import io.theshire.admin.domain.MicroService;

import java.util.Optional;
import java.util.Set;


public interface MicroServicesListingService {

 
  Set<MicroService> listMicroServices();

 
  Optional<MicroService> getMicroService(final String serviceName);

}
