

package io.theshire.authorization.service.impl.user.details;

import io.theshire.common.utils.security.encryptor.CredentialsEncryptor;
import io.theshire.common.utils.security.permission.constants.SecurityDocumentMicroservicePermissionConstants;
import io.theshire.common.utils.security.permission.constants.SecurityPermissionConstants;

import lombok.Builder;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;


@Service
public class UserDetailsSpecialUserMapper {

 
  @Value("${app.cloud.auth.specialUser.user.sub.username}")
  private String userSubscriptionUsername;

 
  @Value("${app.cloud.auth.specialUser.user.sub.password}")
  private String userSubscriptionPassword;

 
  @Value("${app.cloud.auth.specialUser.user.tenantManager.username}")
  private String systemTenantManagerUsername;

 
  @Value("${app.cloud.auth.specialUser.user.tenantManager.password}")
  private String systemTenantManagerPassword;

 
  @Value("${app.cloud.auth.specialUser.user.lostPassword.username}")
  private String lostPasswordSpecialUserUsername;

 
  @Value("${app.cloud.auth.specialUser.user.lostPassword.password}")
  private String lostPasswordSpecialUserPassword;

 
  private final Map<String, SpecialUserInfo> specialUsers = new HashMap<>();

 
  @PostConstruct
  protected void initSpecialUsernames() {
    addUser(userSubscriptionUsername, userSubscriptionPassword,
        SecurityPermissionConstants.SEND_EMAIL,
        SecurityDocumentMicroservicePermissionConstants.DOCUMENT_TEMPLATE_READ);

    addUser(lostPasswordSpecialUserUsername, lostPasswordSpecialUserPassword,
        SecurityPermissionConstants.SEND_EMAIL,
        SecurityDocumentMicroservicePermissionConstants.DOCUMENT_TEMPLATE_READ);

    addUser(systemTenantManagerUsername, systemTenantManagerPassword,
        SecurityPermissionConstants.INDEX_ALL, SecurityPermissionConstants.INDEX_DELETE_ALL,
        SecurityPermissionConstants.TENANT_CREATE, SecurityPermissionConstants.TENANT_REMOVE);
  }

 
  public SpecialUserInfo getSpecialUser(final String username) {
    return specialUsers.get(username);
  }

 
  private void addUser(final String username, final String password, String... permissions) {
    final SpecialUserInfo specialUserInfo = SpecialUserInfo.builder().username(username)
        .password(password).gas(permissionStringToGrantedAuthorities(permissions)).build();
    specialUsers.put(username, specialUserInfo);
  }

 
  private Set<GrantedAuthority> permissionStringToGrantedAuthorities(final String... permissions) {
    final Set<GrantedAuthority> gas = new HashSet<>();
    for (final String permission : permissions) {
      gas.add(new SimpleGrantedAuthority(permission));
    }
    return gas;
  }

 
  @Getter


  @Builder
  protected static class SpecialUserInfo {

   
    private final String username;

   
    private final String password;

   
    private final Set<GrantedAuthority> gas;

   
    private static final CredentialsEncryptor credsEncryptor = new CredentialsEncryptor();

   
    private SpecialUserInfo(String username, String password, Set<GrantedAuthority> gas) {
      super();
      this.username = username;
      this.password = credsEncryptor.encode(password);
      this.gas = gas;
    }
  }

}
