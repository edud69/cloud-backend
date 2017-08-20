

package io.theshire.authorization.server.session;

import io.theshire.common.server.tenant.TenantDatabaseSchema;
import io.theshire.common.utils.security.authentication.AuthenticationContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;


@Component
public class PersistentSessionStrategy implements SessionStrategy {

 
  public static final String TENANT_ID = "tenantId";

 
  private static final String OAUTH2STATE = "oauth2State";

 
  private static final String SQL_INSERT =
      "INSERT INTO " + TenantDatabaseSchema.SINGLE_SIGN_ON_SCHEMA
          + ".oauth_states (state, tenant_id) VALUES (?, ?)";

 
  private static final String SQL_SELECT = "SELECT tenant_id FROM "
      + TenantDatabaseSchema.SINGLE_SIGN_ON_SCHEMA + ".oauth_states WHERE state = ?";

 
  private static final String SQL_DELETE =
      "DELETE FROM " + TenantDatabaseSchema.SINGLE_SIGN_ON_SCHEMA + ".oauth_states WHERE state = ?";

 
  @Autowired
  private DataSource datasource;

 
  private JdbcTemplate jdbcTemplate;

 
  @PostConstruct
  protected void postConstruct() {
    this.jdbcTemplate = new JdbcTemplate(datasource);
  }


  @Override
  public void setAttribute(RequestAttributes request, String name, Object value) {
    if (OAUTH2STATE.equals(name)) {
      final String state = (String) value;
      jdbcTemplate.update(SQL_INSERT,
          new Object[] { state, AuthenticationContext.get().getTenantIdentifier() });
    } else {
      throw new UnsupportedOperationException(
          "PersistentSessionStrategy is not available for other arguments than OAuth2.");
    }
  }


  @Override
  public Object getAttribute(RequestAttributes request, String name) {
    return request.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
  }

 
  public Object getAttribute(String state, String name) {
    if (TENANT_ID.equals(name)) {
      RequestContextHolder.currentRequestAttributes().setAttribute(OAUTH2STATE, state,
          RequestAttributes.SCOPE_REQUEST);
      return jdbcTemplate.query(SQL_SELECT, new Object[] { state }, (rs) -> {
        rs.next();
        return rs.getString("tenant_id");
      });
    } else {
      throw new UnsupportedOperationException(
          "PersistentSessionStrategy does not support other name than tenantId.");
    }
  }


  @Override
  public void removeAttribute(RequestAttributes request, String name) {
    final String state =
        (String) request.getAttribute(OAUTH2STATE, RequestAttributes.SCOPE_REQUEST);
    jdbcTemplate.update(SQL_DELETE, new Object[] { state });
  }

}
