

package io.theshire.common.server.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;


public final class TenantResolver implements CurrentTenantIdentifierResolver {

 
  protected static final String RESERVED_TENANT_IDENTIFIER = String.valueOf(Integer.MIN_VALUE);

 
  private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

 
  public static void setTenantIdentifier(final String tenantIdentifier) {
    threadLocal.set(tenantIdentifier);
  }

 
  public static void clearTenantIdentifier() {
    threadLocal.remove();
  }

 
  public static String getTenantIdentifier() {
    return threadLocal.get();
  }


  @Override
  public String resolveCurrentTenantIdentifier() {
    final String tid = threadLocal.get();
    return tid == null ? RESERVED_TENANT_IDENTIFIER : tid;
  }


  @Override
  public boolean validateExistingCurrentSessions() {
    return threadLocal.get() != null;
  }
}