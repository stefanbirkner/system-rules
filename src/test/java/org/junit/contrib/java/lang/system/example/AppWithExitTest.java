package org.junit.contrib.java.lang.system.example;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class AppWithExitTest {
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	@Test
	public void exits() {
		exit.expectSystemExit();
		AppWithExit.doSomethingAndExit();
	}

	@Test
	public void exitsWithStatusCode1() {
		exit.expectSystemExitWithStatus(1);
		AppWithExit.doSomethingAndExit();
	}

	@Test
	public void writesMessage() {
		exit.expectSystemExitWithStatus(1);
		exit.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() {
				assertEquals("exit ...", AppWithExit.message);
			}
		});
		AppWithExit.doSomethingAndExit();
	}

	@Test
	public void systemExitWithStatusCode1() {
		exit.expectSystemExitWithStatus(1);
		AppWithExit.doSomethingAndExit();
	}

	@Test
	public void noSystemExit() {
		AppWithExit.doNothing();
		// passes
	}
}
