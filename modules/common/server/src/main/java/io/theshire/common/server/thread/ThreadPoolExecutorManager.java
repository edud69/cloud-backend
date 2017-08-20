

package io.theshire.common.server.thread;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;


@Slf4j
@Component
@Configuration
@EnableAsync
public class ThreadPoolExecutorManager extends ThreadPoolTaskExecutor {

 
  private static final long serialVersionUID = -1145378350704902566L;

 
  public ThreadPoolExecutorManager() {
    super();
    final int poolSize = poolSize();
    this.setCorePoolSize(poolSize);
    this.setMaxPoolSize(poolSize);
    log.info("ThreadPoolTaskExecutor with poolsize of {}.", poolSize);
  }

 
  @Autowired(required = false)
  private transient ThreadLocalAwareRunnersCreator threadLocalAwareRunnersCreator;


  @PreDestroy
  @Override
  public void shutdown() {
    log.info("Shutting down executors.");

    try {
      super.shutdown();
    } catch (final Exception exeption) {
      log.warn("Failed to shutdown default executor, {}.", exeption.getMessage());
    }
  }


  @Override
  public void execute(Runnable task) {
    super.execute(decorateWithTenantCtx(task));
  }


  @Override
  public void execute(Runnable task, long startTimeout) {
    super.execute(decorateWithTenantCtx(task), startTimeout);
  }


  @Override
  public <T> Future<T> submit(Callable<T> task) {
    return super.submit(decorateWithTenantCtx(task));
  }


  @Override
  public Future<?> submit(Runnable task) {
    return super.submit(decorateWithTenantCtx(task));
  }


  @Override
  public ListenableFuture<?> submitListenable(Runnable task) {
    return super.submitListenable(decorateWithTenantCtx(task));
  }


  @Override
  public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
    return super.submitListenable(decorateWithTenantCtx(task));
  }

 
  private Runnable decorateWithTenantCtx(Runnable task) {
    Runnable toRun = task;
    if (threadLocalAwareRunnersCreator != null) {
      toRun = threadLocalAwareRunnersCreator.decorateWithThreadLocalContext(task);
    }
    return toRun;
  }

 
  private <T> Callable<T> decorateWithTenantCtx(Callable<T> task) {
    Callable<T> toRun = task;
    if (threadLocalAwareRunnersCreator != null) {
      toRun = threadLocalAwareRunnersCreator.decorateWithThreadLocalContext(task);
    }
    return toRun;
  }

 
  private static int poolSize() {
    final int processors = Runtime.getRuntime().availableProcessors();
    final int poolsize = processors * 2 + 1;
    log.info("Setting ThreadPoolExecutorManager with a pool size of {} threads.", poolsize);
    return poolsize;
  }

}
