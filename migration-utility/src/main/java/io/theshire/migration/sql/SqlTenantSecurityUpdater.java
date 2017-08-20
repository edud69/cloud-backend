

package io.theshire.migration.sql;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.sql.DataSource;


public class SqlTenantSecurityUpdater {

 
  private final String schemaName;

 
  private JdbcTemplate jdbcTemplate;

 
  private final TransactionTemplate transactionTemplate;

 
  public SqlTenantSecurityUpdater(final DataSource ds, final String schemaName) {
    final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(ds);
    this.schemaName = schemaName;
    jdbcTemplate = new JdbcTemplate(transactionManager.getDataSource());
    transactionTemplate = new TransactionTemplate(transactionManager);
  }

 
  public void executeInTransaction() {
    transactionTemplate.execute(new TransactionCallback<Object>() {

      @Override
      public Object doInTransaction(final TransactionStatus status) {
        final List<String> queries = new ArrayList<>();

        queries.add("SET schema '" + SqlTenantSecurityUpdater.this.schemaName + "'");

        // extract roles and perms from a data provider...
        final SecurityData data = extractData();
        // 1. add new permissions to DB
        final Set<String> permissions = data.getPermissions();
        queries.addAll(Arrays.asList(addNewPermissions(permissions)));
        // 2. add new roles to DB
        final Set<RoleData> roleDatas = data.getRoleDatas();
        final Set<String> roles =
            roleDatas.stream().map(r -> r.getRolename()).collect(Collectors.toSet());
        queries.addAll(Arrays.asList(addNewRoles(roles)));
        // 3. link all roles in DB with permissions
        queries.addAll(linkRolesWithPermissions(roleDatas));
        // 4. remove old permissions from DB
        queries.add(removeDeprecatedPermissions(permissions));
        // 5. migrate users from a deprecated role to a new role
        queries.addAll(migrateUsersToNewRoles(data.getDeprecatedRoleMapper()));
        // 6. remove old roles from DB
        queries.add(removeDeprecatedRoles(roles));

        return jdbcTemplate.batchUpdate(queries.toArray(new String[queries.size()]));
      }
    });
  }

 
  private List<String> migrateUsersToNewRoles(Map<String, String> deprecatedRolesMapper) {
    final List<String> queries = new ArrayList<>();

    deprecatedRolesMapper.entrySet().forEach(e -> {
      final String roleIdSubQuery =
          String.format("SELECT id FROM roles WHERE role_name = '%s'", e.getValue());
      final String roleIdSubQuery2 =
          String.format("SELECT id FROM roles WHERE role_name = '%s'", e.getKey());
      final String sql = String.format(
          "UPDATE users_roles SET role_id = (%s) "
              + "WHERE id IN (SELECT id FROM users_roles WHERE role_id = (%s))",
          roleIdSubQuery, roleIdSubQuery2);
      queries.add(sql);
    });
    return queries;
  }

 
  private String[] addNewRoles(final Set<String> rolenames) {
    return insertIfNotExists("roles", "role_name", rolenames);
  }

 
  private String[] addNewPermissions(final Set<String> permissions) {
    return insertIfNotExists("permissions", "permission_name", permissions);
  }

 
  private String removeDeprecatedPermissions(final Set<String> permissionsToKeep) {
    return removeDeprecatedEntry("permissions", "permission_name", permissionsToKeep);
  }

 
  private String removeDeprecatedRoles(final Set<String> rolesToKeep) {
    return removeDeprecatedEntry("roles", "role_name", rolesToKeep);
  }

 
  private String removeDeprecatedEntry(final String tableName, final String colName,
      final Set<String> valuesToKeep) {
    final String sql = String.format("DELETE FROM %s WHERE %s NOT IN (%s)", tableName, colName,
        toSqlInClause(valuesToKeep));
    return sql;
  }

 
  private List<String> linkRolesWithPermissions(final Set<RoleData> roleDatas) {
    final List<String> queries = new ArrayList<>();

    roleDatas.forEach(roleData -> {
      final Set<String> permissions = roleData.getRelatedPermissions();
      final String sqlDelete = String.format("DELETE FROM roles_permissions WHERE role_id = "
          + "(SELECT id FROM roles WHERE role_name = '%s')", roleData.getRolename());
      queries.add(sqlDelete);

      permissions.forEach(permission -> {
        final String sqlInsert = String.format(
            "INSERT INTO roles_permissions (role_id, permission_id) VALUES "
                + "((SELECT id FROM roles WHERE role_name = '%s'), "
                + "(SELECT id FROM permissions WHERE permission_name = '%s'))",
            roleData.getRolename(), permission);
        queries.add(sqlInsert);

      });

    });
    return queries;
  }

 
  private SecurityData extractData() {
    try {
      return new SqlSecurityDataExtractor().extractData();
    } catch (final Exception exception) {
      throw new RuntimeException(exception);
    }
  }

 
  private String toSqlInClause(final Set<String> values) {
    final AtomicInteger count = new AtomicInteger(0);
    final int total = values.size();
    final StringBuilder sb = new StringBuilder();

    values.stream().forEach(value -> {
      sb.append("'");
      sb.append(value);
      sb.append("'");
      if (count.get() < total - 1) {
        sb.append(",");
      }
      count.incrementAndGet();
    });

    return sb.toString();
  }

 
  private String[] insertIfNotExists(final String tableName, final String colName,
      final Set<String> values) {
    final String[] sqls = new String[values.size()];
    final AtomicInteger idx = new AtomicInteger(-1);

    values.forEach(permName -> {
      final String sql = String.format(
          "INSERT INTO %s (%s) (SELECT '%s' WHERE NOT EXISTS (SELECT %s FROM %s WHERE %s = '%s'))",
          tableName, colName, permName, colName, tableName, colName, permName);
      sqls[idx.incrementAndGet()] = sql;
    });

    return sqls;
  }

 
  @Getter


  @EqualsAndHashCode
  protected static class RoleData {

   
    private final String rolename;

   
    private Set<String> relatedPermissions;

   
    protected RoleData(String rolename, Set<String> relatedPermissions) {
      super();
      this.rolename = rolename;
      this.relatedPermissions = relatedPermissions;
    }

  }

 
  @Getter


  @EqualsAndHashCode
  protected static class SecurityData {

   
    private final Set<RoleData> roleDatas;

   
    private final Set<String> permissions;

   
    private final Map<String, String> deprecatedRoleMapper;

   
    protected SecurityData(Set<RoleData> roleDatas, Set<String> permissions,
        Map<String, String> deprecatedRoleMapper) {
      super();
      this.roleDatas = roleDatas;
      this.permissions = permissions;
      this.deprecatedRoleMapper = deprecatedRoleMapper;
    }

  }

}
