

package io.theshire.common.utils.sql;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;


public class SqlDatabaseCreatorTest {

 
  private SqlDatabaseCreator classUnderTest;

 
  private String username = "username";

 
  private String password = "password";

 
  private String originalUrl = "jdbc:postgresql://localhost:5432/authentication_master";

 
  private String maintenanceDbName = "postgres";

 
  private int maxTenantPerDb = 10;

 
  private String driverClassName = "org.postgresql.Driver";

 
  private JdbcTemplate jdbcTemplate;

 
  @Before
  public void setup()
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    this.classUnderTest = Mockito.spy(new SqlDatabaseCreator(username, password, originalUrl,
        maintenanceDbName, maxTenantPerDb, driverClassName));
    this.jdbcTemplate = Mockito.mock(JdbcTemplate.class);
    Mockito.doReturn(jdbcTemplate).when(classUnderTest).createJdbcTemplate();
  }

 
  @Test
  public void shouldNotCreateDatabaseWhenExists() throws Exception {
    final List<Map<String, Object>> rez =
        Lists.newArrayList(Maps.newHashMap("datName", "a-database-name"));
    Mockito.when(
        jdbcTemplate.queryForList(SqlDatabaseCreator.SQL_GET_DATABASE_WITH_AVAILABLE_TENANT_SPACE,
            new Object[] { "authentication_slave%", maxTenantPerDb }))
        .thenReturn(rez);

    final String dbUrl = classUnderTest.getDatabaseNameAndCreateIfNeeded();
    Assert.assertEquals("jdbc:postgresql://localhost:5432/a-database-name", dbUrl);
  }

 
  @Test
  public void shouldCreateDatabaseWhenNoMoreSpaceForTenant() throws Exception {
    final List<Map<String, Object>> rez = Lists.newArrayList();
    Mockito.when(
        jdbcTemplate.queryForList(SqlDatabaseCreator.SQL_GET_DATABASE_WITH_AVAILABLE_TENANT_SPACE,
            new Object[] { "authentication_slave%", maxTenantPerDb }))
        .thenReturn(rez);

    final Integer lastDbId = 123;
    Mockito
        .when(jdbcTemplate.queryForObject(SqlDatabaseCreator.SQL_GET_NEXT_AVAILABLE_SLAVE_DB_ID,
            Integer.class, new Object[] { "authentication_slave", "authentication_slave%" }))
        .thenReturn(lastDbId);

    final String dbUrl = classUnderTest.getDatabaseNameAndCreateIfNeeded();
    Assert.assertEquals("jdbc:postgresql://localhost:5432/authentication_slave124", dbUrl);

    Mockito.verify(jdbcTemplate)
        .execute("CREATE DATABASE authentication_slave124 WITH TEMPLATE authentication_template");
  }

}
