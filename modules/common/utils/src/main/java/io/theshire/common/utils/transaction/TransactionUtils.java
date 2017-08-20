

package io.theshire.common.utils.transaction;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Component
public class TransactionUtils {

 
  public void runAfterCommit(final Runnable runnable) {
    final TransactionSynchronizationAdapter adapter = afterCommitAdapter(runnable);
    TransactionSynchronizationManager.registerSynchronization(adapter);
  }

 
  public void runAfterRollback(final Runnable runnable) {
    final TransactionSynchronizationAdapter adapter = afterRollbackAdapter(runnable);
    TransactionSynchronizationManager.registerSynchronization(adapter);
  }

 
  protected TransactionSynchronizationAdapter afterRollbackAdapter(final Runnable runnable) {
    return new TransactionSynchronizationAdapter() {
      public void afterCompletion(int status) {
        if (status == STATUS_ROLLED_BACK) {
          runnable.run();
        }
      }
    };
  }

 
  protected TransactionSynchronizationAdapter afterCommitAdapter(final Runnable runnable) {
    return new TransactionSynchronizationAdapter() {
      public void afterCommit() {
        runnable.run();
      }
    };
  }

}
