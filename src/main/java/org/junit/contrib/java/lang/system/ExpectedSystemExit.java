package org.junit.contrib.java.lang.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


import org.junit.contrib.java.lang.system.internal.NoExitSecurityManager;
import org.junit.contrib.java.lang.system.internal.CheckExitCalled;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * The {@code ExpectedSystemExit} allows in-test specification of expected
 * {@code System.exit(...)} calls.
 * 
 * <p>
 * The following tests pass.
 * 
 * <pre>
 *   public void MyTest {
 *     &#064;Rule
 *     public final ExpectedSystemExit exit = ExpectedSystemExit.none();
 * 
 *     &#064;Test
 *     public void noSystemExit() {
 *       //passes
 *     }
 * 
 *     &#064;Test
 *     public void systemExitWithArbitraryStatusCode() {
 *       exit.expectSystemExit();
 *       System.exit(0);
 *     }
 * 
 *     &#064;Test
 *     public void systemExitWithSelectedStatusCode0() {
 *       exit.expectSystemExitWithStatus(0);
 *       System.exit(0);
 *     }
 *   }
 * </pre>
 */
public class ExpectedSystemExit implements TestRule {
	public static ExpectedSystemExit none() {
		return new ExpectedSystemExit();
	}

	private boolean expectExit = false;
	private Integer expectedStatus = null;

	private ExpectedSystemExit() {
	}

	public void expectSystemExitWithStatus(int status) {
		expectSystemExit();
		expectedStatus = status;
	}

	public void expectSystemExit() {
		expectExit = true;
	}

	public Statement apply(final Statement base, Description description) {
		ProvideSecurityManager provideNoExitSecurityManager = new ProvideSecurityManager(
				new NoExitSecurityManager());
		Statement statement = new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					base.evaluate();
					handleMissingSystemExit();
				} catch (CheckExitCalled e) {
					handleSystemExit(e);
				}
			}
		};
		return provideNoExitSecurityManager.apply(statement, description);
	}

	private void handleMissingSystemExit() {
		if (expectExit)
			fail("System.exit has not been called.");
	}

	private void handleSystemExit(CheckExitCalled e) {
		if (!expectExit)
			fail("Unexpected call of System.exit(" + e.status + ").");
		else if (expectedStatus != null)
			assertEquals("Wrong exit status", expectedStatus, e.status);
	}
}