

package io.theshire.admin.service.tenant;

import io.theshire.admin.domain.tenant.TenantStateEntry;

import java.util.Set;


public interface TenantStateService {

 
  Set<TenantStateEntry> getTenantStates();

}
