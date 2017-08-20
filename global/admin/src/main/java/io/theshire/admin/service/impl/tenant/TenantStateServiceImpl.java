

package io.theshire.admin.service.impl.tenant;

import io.theshire.admin.domain.MicroService;
import io.theshire.admin.domain.MicroServiceSqlDbConfig;
import io.theshire.admin.domain.tenant.TenantStateEntry;
import io.theshire.admin.service.microservices.MicroServicesListingService;
import io.theshire.admin.service.tenant.TenantStateService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Service;

import java.sql.Driver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service


@Slf4j
class TenantStateServiceImpl implements TenantStateService {

 
  private static final String SQL_QUERY_TENANT_CONFIG_LIST =
      "select tenant_id from configurations.tenant_datasources";

 
  @Autowired
  private MicroServicesListingService microServiceListingService;


  @Override
  public Set<TenantStateEntry> getTenantStates() {
    final Map<String, TenantStateEntry> tenantStateEntries = new HashMap<>();
    microServiceListingService.listMicroServices().stream()
        .forEach(ms -> process(ms, tenantStateEntries));
    return tenantStateEntries.values().stream().collect(Collectors.toSet());
  }

 
  private void process(final MicroService microService,
      final Map<String, TenantStateEntry> tenantStateEntries) {
    // get list of tenants for each micro services
    if (microService.hasSqlActive()) {
      getSqlConfiguredTenants(microService.getDbConfig()).forEach(tenant -> {
        if (tenantStateEntries.containsKey(tenant)) {
          tenantStateEntries.get(tenant).addActiveSqlMicroService(microService.getServiceName());
        } else {
          final TenantStateEntry tEntry = new TenantStateEntry(tenant,
              new HashSet<>(Arrays.asList(microService.getServiceName())));
          tenantStateEntries.put(tenant, tEntry);
        }
      });
    }
  }

 
  private Set<String> getSqlConfiguredTenants(final MicroServiceSqlDbConfig dbConfig) {
    final Set<String> activeTenants = new HashSet<>();

    // list all tenant entries from dbConfig
    try {
      final SimpleDriverDataSource ds = new SimpleDriverDataSource(
          (Driver) Class.forName(dbConfig.getDriverClassName()).newInstance(), dbConfig.getUrl(),
          dbConfig.getUsername(), dbConfig.getPassword());
      final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
      final List<String> tenants =
          jdbcTemplate.queryForList(SQL_QUERY_TENANT_CONFIG_LIST, String.class);
      activeTenants.addAll(tenants);
    } catch (final Exception exc) {
      log.error("Exception occurred, ", exc);
    }

    return activeTenants;
  }

}
