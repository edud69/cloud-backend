
package io.theshire.common.server.tenant.manager;

import io.theshire.common.server.tenant.TenantResolver;
import io.theshire.common.service.infrastructure.indexation.IndexationTenantResolver;

import org.springframework.stereotype.Component;

@Component
class TenantResolverDefault implements IndexationTenantResolver {
  @Override
  public String resolveTenantId() {
    return TenantResolver.getTenantIdentifier();
  }
}
