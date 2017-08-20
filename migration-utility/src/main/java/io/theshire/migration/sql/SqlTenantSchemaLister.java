

package io.theshire.migration.sql;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;


public class SqlTenantSchemaLister {

 
  private static final String TENANT_PREFIX_SCHEMA = "tenant";

 
  public static List<String> getTenantSchemas(final DataSource ds) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    final Set<String> schemas = getExistingSchemas(jdbcTemplate);
    schemas.addAll(getConfiguredSchemas(jdbcTemplate));
    return schemas.stream().collect(Collectors.toList());
  }

 
  private static Set<String> getConfiguredSchemas(final JdbcTemplate jdbcTemplate) {
    if (!tenantConfigurationTableExists(jdbcTemplate)) {
      return new HashSet<>();
    }

    final String currentDsUrl =
        org.apache.tomcat.jdbc.pool.DataSource.class.cast(jdbcTemplate.getDataSource()).getUrl();

    final List<Map<String, Object>> schemas = jdbcTemplate.queryForList(
        "select tenant_id from configurations.tenant_datasources where url = ?",
        new Object[] { currentDsUrl });
    return schemas.stream().map(s -> TENANT_PREFIX_SCHEMA + (String) s.get("tenant_id"))
        .collect(Collectors.toSet());
  }

 
  private static boolean tenantConfigurationTableExists(final JdbcTemplate jdbcTemplate) {
    final String sql = "SELECT EXISTS (SELECT 1 " + "FROM information_schema.tables "
        + "WHERE table_schema = 'configurations' " + "AND table_name = 'tenant_datasources')";
    return jdbcTemplate.queryForObject(sql, Boolean.class);
  }

 
  private static Set<String> getExistingSchemas(final JdbcTemplate jdbcTemplate) {
    final List<Map<String, Object>> schemas = jdbcTemplate.queryForList(
        "select schema_name from information_schema.schemata where schema_name like ?",
        TENANT_PREFIX_SCHEMA + "%");
    return schemas.stream().map(s -> (String) s.get("schema_name")).collect(Collectors.toSet());
  }

}
