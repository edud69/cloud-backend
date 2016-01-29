package com.biohiit.common.util.transaction;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionUtils {

	public static final void runAfterCommit(final Runnable runnable) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			public void afterCommit() {
				// do what you want to do after commit
				runnable.run();
			}

			public void afterCompletion(int arg0) {
				// TODO Auto-generated method stub

			}

			public void beforeCommit(boolean arg0) {
				// TODO Auto-generated method stub

			}

			public void beforeCompletion() {
				// TODO Auto-generated method stub

			}

			public void flush() {
				// TODO Auto-generated method stub

			}

			public void resume() {
				// TODO Auto-generated method stub

			}

			public void suspend() {
				// TODO Auto-generated method stub

			}
		});
	}

}
