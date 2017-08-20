

package io.theshire.common.server.tenant.manager;


public interface TenantManager {

 
  void processQuery(final TenantStateChangeMessage tenantStateChangeMessage) throws Exception;

}
