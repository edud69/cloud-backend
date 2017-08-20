

package io.theshire.common.utils.oauth2.resource.identifier;

import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public enum OAuth2ResourceIdentifier {

 
  AccountService("account_access", "account-service"),

 
  AuthService("authentication_access", "auth-service"),

 
  AdminPanel("admin_panel_access", "admin-panel"),

 
  DocumentService("document_access", "document-service"),

 
  ChatService("chat_access", "chat-service"),

 
  ConfigService("config_access", "config-service"),

 
  TurbineService("turbine_server_access", "turbine-server");

 
  public static Set<OAuth2ResourceIdentifier> enumerateAdminServices() {
    final Set<OAuth2ResourceIdentifier> adminServices = new HashSet<>();
    adminServices.add(AdminPanel);
    return Collections.unmodifiableSet(adminServices);
  }

 

 

 
  @Getter
  private final String resourceId;

 

 

 
  @Getter
  private final String microserviceName;

 
  private OAuth2ResourceIdentifier(final String resourceId, final String microServiceName) {
    this.resourceId = resourceId;
    this.microserviceName = microServiceName;
  }


  @Override
  public String toString() {
    return this.microserviceName;
  }

}
