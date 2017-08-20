

package io.theshire.common.service.infrastructure.impersonification;

import java.util.concurrent.Callable;


public interface ImpersonificationService {

 
  void runWithImpersonated(final Runnable runnable, final String username, final String password);

 
  <T> T callWithImpersonated(final Callable<T> callable, final String username,
      final String password);

}
