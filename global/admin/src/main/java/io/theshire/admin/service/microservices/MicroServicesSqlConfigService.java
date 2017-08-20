

package io.theshire.admin.service.microservices;

import io.theshire.admin.domain.MicroServiceSqlDbConfig;


public interface MicroServicesSqlConfigService {

 
  MicroServiceSqlDbConfig retrieveSqlConfig(final String serviceName);

}
