

package io.theshire.admin.domain;

import io.theshire.common.domain.DomainObject;

import lombok.Getter;


public class MicroService extends DomainObject {

 
  private static final long serialVersionUID = 547397814677330935L;

 

 
  @Getter
  private final String serviceName;

 
  @Getter
  private MicroServiceSqlDbConfig dbConfig;

 
  public boolean hasSqlActive() {
    return dbConfig != null;
  }

 
  public MicroService(final String serviceName) {
    this.serviceName = serviceName;
  }

 
  public MicroService(final String serviceName, final MicroServiceSqlDbConfig dbConfig) {
    this(serviceName);
    this.dbConfig = dbConfig;
  }

}
