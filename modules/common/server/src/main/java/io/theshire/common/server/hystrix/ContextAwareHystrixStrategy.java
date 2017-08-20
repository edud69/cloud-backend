

package io.theshire.common.server.hystrix;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;

import io.theshire.common.server.tenant.TenantResolver;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;


@Component
class ContextAwareHystrixStrategy extends HystrixConcurrencyStrategy {

 
  @PostConstruct
  protected void registerInstance() {
    HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
  }

 
  private void clearThreadLocals() {
    SecurityContextHolder.clearContext();
    TenantResolver.clearTenantIdentifier();
  }


  @Override
  public <T> Callable<T> wrapCallable(Callable<T> callable) {
    final String tenantId = TenantResolver.getTenantIdentifier();
    final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    return new Callable<T>() {

      @Override
      public T call() throws Exception {
        try {
          TenantResolver.setTenantIdentifier(tenantId);
          SecurityContextHolder.getContext().setAuthentication(auth);

          return callable != null ? callable.call() : null;
        } finally {
          clearThreadLocals();
        }
      }

    };
  }


  @Override
  public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
    return new HystrixRequestVariableDefault<T>() {
      @Override
      public T get() {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
          return null;
        }
        return super.get();
      }
    };
  }
}