

package io.theshire.common.utils.transaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@RunWith(PowerMockRunner.class)
@PrepareForTest(TransactionSynchronizationManager.class)
public class TransactionUtilsTest {

 
  private TransactionUtils classUnderTest = new TransactionUtils();

 
  @Test
  public void testRunAfterCommit() throws Exception {
    final Runnable r = Mockito.mock(Runnable.class);
    PowerMockito.mockStatic(TransactionSynchronizationManager.class);
    PowerMockito.doNothing().when(TransactionSynchronizationManager.class);
    classUnderTest.runAfterCommit(r);
  }

 
  @Test
  public void testAfterCommitAdapter() throws Exception {
    final Runnable r = Mockito.mock(Runnable.class);
    final TransactionSynchronizationAdapter adapter = classUnderTest.afterCommitAdapter(r);
    adapter.afterCommit();
    Mockito.verify(r).run();

  }

}
