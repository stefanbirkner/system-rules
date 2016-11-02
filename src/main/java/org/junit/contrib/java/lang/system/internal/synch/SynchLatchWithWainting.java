package org.junit.contrib.java.lang.system.internal.synch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class SynchLatchWithWainting implements SynchLatch {

	private final long timeout;
	private final CountDownLatch latch;

	public SynchLatchWithWainting(long timeout) {
		this.timeout = timeout;
		latch = new CountDownLatch(1);
	}

	public void goAhead() {
		latch.countDown();
	}

	public void await() throws InterruptedException {
		latch.await(timeout, TimeUnit.MILLISECONDS);
	}
}
