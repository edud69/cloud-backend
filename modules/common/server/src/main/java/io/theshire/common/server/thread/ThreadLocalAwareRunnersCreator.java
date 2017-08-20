

package io.theshire.common.server.thread;

import io.theshire.common.server.tenant.TenantResolver;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;


@Component
public class ThreadLocalAwareRunnersCreator {

 
  private void clearThreadLocals() {
    SecurityContextHolder.clearContext();
    TenantResolver.clearTenantIdentifier();
  }

 
  private void setThreadLocals(final String tenantIdentifier, final Authentication authentication) {
    TenantResolver.setTenantIdentifier(tenantIdentifier);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

 
  public Runnable decorateWithThreadLocalContext(Runnable runnable) {
    final String parentThreadTenantIdentifier = TenantResolver.getTenantIdentifier();
    final Authentication parentThreadAuthentication = SecurityContextHolder.getContext()
        .getAuthentication();
    return new Runnable() {

      private final String threadTenantIdentifier = parentThreadTenantIdentifier;

      private final Authentication threadAuthentication = parentThreadAuthentication;

      @Override
      public void run() {
        try {
          setThreadLocals(threadTenantIdentifier, threadAuthentication);
          runnable.run();
        } finally {
          clearThreadLocals();
        }
      }
    };
  }

 
  public <T> Callable<T> decorateWithThreadLocalContext(Callable<T> callable) {
    final String parentThreadTenantIdentifier = TenantResolver.getTenantIdentifier();
    final Authentication parentThreadAuthentication = SecurityContextHolder.getContext()
        .getAuthentication();
    return new Callable<T>() {

      private final String threadTenantIdentifier = parentThreadTenantIdentifier;

      private final Authentication threadAuthentication = parentThreadAuthentication;

      @Override
      public T call() throws Exception {
        try {
          setThreadLocals(threadTenantIdentifier, threadAuthentication);
          return callable.call();
        } finally {
          clearThreadLocals();
        }
      }
    };
  }

}
