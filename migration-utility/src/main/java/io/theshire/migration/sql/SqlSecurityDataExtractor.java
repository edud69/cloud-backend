

package io.theshire.migration.sql;

import io.theshire.migration.sql.SqlTenantSecurityUpdater.RoleData;
import io.theshire.migration.sql.SqlTenantSecurityUpdater.SecurityData;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SqlSecurityDataExtractor {

  private static volatile SecurityData cachedData;

  private static Object lock = new Object();

 
  public SecurityData extractData() throws Exception {
    if (cachedData != null) {
      return cachedData;
    }

    synchronized (lock) {
      if (cachedData != null) {
        return cachedData;
      }

      final PathMatchingResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver();
      final Resource roleMapper = pmrpr
          .getResource("classpath:security/roles_permissions-mapper.csv");
      final Map<String, Set<String>> mappedRoles = loadCsvFile(roleMapper);
      final Set<RoleData> roleDatas = toRoleData(mappedRoles);

      // TODO
      final Map<String, String> mappedDeprecatedRoles = new HashMap<>();

      cachedData = new SecurityData(roleDatas, roleDatas.stream()
          .flatMap(r -> r.getRelatedPermissions().stream()).collect(Collectors.toSet()),
          mappedDeprecatedRoles);

      return cachedData;
    }
  }

  private Set<RoleData> toRoleData(final Map<String, Set<String>> mappedRoles) {
    final Set<RoleData> roleDatas = new HashSet<>();
    mappedRoles.entrySet().forEach(e -> roleDatas.add(new RoleData(e.getKey(), e.getValue())));
    return roleDatas;
  }

  private Map<String, Set<String>> loadCsvFile(Resource csv) throws IOException {
    boolean header = true;

    final List<String> rolenamesOrdered = new ArrayList<>();
    final Map<String, Set<String>> rolePerms = new HashMap<>();

    try (final BufferedReader br = new BufferedReader(
        new InputStreamReader(csv.getInputStream(), Charset.forName("UTF-8")), 4096)) {
      String line;
      while ((line = br.readLine()) != null) {
        if (!StringUtils.isEmpty(line)) {
          final List<String> content = readLine(line);
          if (header) {
            content.forEach(rolename -> {
              rolePerms.put(rolename, new HashSet<>());
              rolenamesOrdered.add(rolename);
            });
            header = false;
          } else {
            final String permissionName = content.get(0);
            IntStream.range(1, content.size()).forEach(idx -> {
              final Set<String> permissions = rolePerms.get(rolenamesOrdered.get(idx - 1));
              if (content.get(idx).trim().equalsIgnoreCase("x")) {
                permissions.add(permissionName);
              }
            });
          }
        }
      }
    }

    return rolePerms;
  }

  private List<String> readLine(String line) {
    List<String> items = new ArrayList<>();
    items.addAll(Arrays.asList(line.split("\\s*,\\s*")));
    return items;
  }

}
