package org.junit.contrib.java.lang.system.internal.synch;

public interface SynchLatch {
	void goAhead();
	void await() throws InterruptedException;
}
