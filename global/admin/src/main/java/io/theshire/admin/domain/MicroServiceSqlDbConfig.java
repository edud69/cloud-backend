

package io.theshire.admin.domain;

import io.theshire.common.domain.DomainObject;

import lombok.Getter;


@Getter
public class MicroServiceSqlDbConfig extends DomainObject {

  private static final long serialVersionUID = -2669715143076124776L;

  private final String url;

  private final String username;

  private final String password;

  private final String driverClassName;

 
  public MicroServiceSqlDbConfig(final String url, final String username, final String password,
      final String driverClassName) {
    this.url = url;
    this.username = username;
    this.password = password;
    this.driverClassName = driverClassName;
  }

}
