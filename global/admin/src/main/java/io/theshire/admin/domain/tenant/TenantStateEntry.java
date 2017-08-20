

package io.theshire.admin.domain.tenant;

import io.theshire.common.domain.DomainObject;
import io.theshire.common.utils.tenant.TenantIdValidator;

import lombok.Getter;

import org.springframework.util.Assert;

import java.util.Set;


public class TenantStateEntry extends DomainObject {

 
  private static final long serialVersionUID = -4059460541649735643L;

 
  @Getter
  private final Set<String> activeSqlMicroservices;

 
  @Getter
  private final String tenantId;

 
  public TenantStateEntry(final String tenantId, final Set<String> activeSqlMicroservices) {
    Assert.notNull(activeSqlMicroservices, "activeSqlMicroservices cannot be null.");
    TenantIdValidator.validate(tenantId);
    this.activeSqlMicroservices = activeSqlMicroservices;
    this.tenantId = tenantId;
  }

 
  public void addActiveSqlMicroService(final String ms) {
    activeSqlMicroservices.add(ms);
  }

}
