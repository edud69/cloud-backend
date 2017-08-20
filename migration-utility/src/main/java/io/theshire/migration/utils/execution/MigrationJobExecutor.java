

package io.theshire.migration.utils.execution;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;




@Slf4j
public class MigrationJobExecutor {

 
  private static final MigrationJobExecutor instance = new MigrationJobExecutor();

 
  public static final List<Future<?>> submitAll(final Collection<Runnable> rs) throws Exception {
    final List<Future<?>> fs = new ArrayList<>(rs.size());
    rs.forEach(r -> fs.add(instance.executorService.submit(r)));
    return fs;
  }

 
  public static final void shutDown() {
    instance.executorService.shutdown();
  }

 
  private final ExecutorService executorService;

 
  private MigrationJobExecutor() {
    executorService = Executors.newFixedThreadPool(poolSize());
  }

 
  private int poolSize() {
    final int processors = Runtime.getRuntime().availableProcessors();
    log.info("Available cpus : " + processors + '.');
    final int poolsize = processors + 1;
    log.info("Core pool size : " + poolsize + " threads.");
    return poolsize;
  }

}
