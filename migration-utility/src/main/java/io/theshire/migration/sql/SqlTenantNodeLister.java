

package io.theshire.migration.sql;

import io.theshire.migration.utils.crypto.CryptoDataDecoder;

import lombok.Getter;
import lombok.Setter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;


public class SqlTenantNodeLister {

 
  private static final String TENANT_NODES_QUERY = "select username, password, url from "
      + SqlServiceMigrationTask.CONFIGURATIONS_SCHEMA + ".tenant_datasources";

 
  @Getter

 
  @Setter
  public static class TenantNode {

   
    private String username;

   
    private String password;

   
    private String url;
  }

 
  public static List<TenantNode> getTenantNodes(final DataSource dataSource) {
    final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    return jdbcTemplate.query(TENANT_NODES_QUERY, new Object[] {}, new RowMapper<TenantNode>() {

    
      @Override
      public TenantNode mapRow(ResultSet rs, int rowNum) throws SQLException {
        final TenantNode tenantNode = new TenantNode();
        tenantNode.setUsername(rs.getString("username"));
        try {
          tenantNode
              .setPassword(CryptoDataDecoder.getCryptoUtils().decrypt(rs.getString("password")));
        } catch (Exception exc) {
          throw new RuntimeException(exc);
        }

        tenantNode.setUrl(rs.getString("url"));
        return tenantNode;
      }

    });
  }

}
