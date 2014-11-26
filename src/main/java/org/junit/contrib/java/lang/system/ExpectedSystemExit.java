package org.junit.contrib.java.lang.system;

import static java.lang.System.getSecurityManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.contrib.java.lang.system.internal.CheckExitCalled;
import org.junit.contrib.java.lang.system.internal.NoExitSecurityManager;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * The {@code ExpectedSystemExit} allows in-test specification of expected
 * {@code System.exit(...)} calls.
 *
 * <p>
 * If your code calls {@code System.exit(),} then your test stops and doesn't
 * finish. The {@code ExpectedSystemExit} rule allows in-test specification of
 * expected {@code System.exit()} calls. Furthermore you cannot use JUnit's
 * assert methods because of the abnormal termination of your code. As a
 * substitute you can provide an {@code Assertion} object to the
 * {@code ExpectedSystemExit} rule.
 *
 * <p>
 * Some care must be taken if your system under test creates a new thread and
 * this thread calls {@code System.exit()}. In this case you have to ensure that
 * the test does not finish before {@code System.exit()} is called.
 *
 * <pre>
 * public class AppWithExit {
 * 	public static String message;
 *
 * 	public static int doSomethingAndExit() {
 * 		message = &quot;exit ...&quot;;
 * 		System.exit(1);
 * 	}
 *
 * 	public static int doNothing() {
 * 	}
 * }
 * </pre>
 *
 * <pre>
 * public void AppWithExitTest {
 *   &#064;Rule
 *   public final ExpectedSystemExit exit = ExpectedSystemExit.none();
 *
 *   &#064;Test
 *   public void exits() {
 *     exit.expectSystemExit();
 *     AppWithExit.doSomethingAndExit();
 *   }
 *
 *   &#064;Test
 *   public void exitsWithStatusCode1() {
 *     exit.expectSystemExitWithStatus(1);
 *     AppWithExit.doSomethingAndExit();
 *   }
 *
 *   &#064;Test
 *   public void writesMessage() {
 *     exit.checkAssertionAfterwards(new Assertion() {
 *       public void checkAssertion() {
 *         assertEquals("exit ...", AppWithExit.message);
 *       }
 *     });
 *     AppWithExit.doSomethingAndExit();
 *   }
 *
 *   &#064;Test
 *   public void systemExitWithStatusCode1() {
 *     exit.expectSystemExitWithStatus(1);
 *     AppWithExit.doSomethingAndExit();
 *   }
 *
 *   &#064;Test
 *   public void noSystemExit() {
 *     AppWithExit.doNothing();
 *     //passes
 *   }
 * }
 * </pre>
 */
public class ExpectedSystemExit implements TestRule {
	public static ExpectedSystemExit none() {
		return new ExpectedSystemExit();
	}

	private final Collection<Assertion> assertions = new ArrayList<Assertion>();
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

	public void checkAssertionAfterwards(Assertion assertion) {
		assertions.add(assertion);
	}

	public Statement apply(final Statement base, Description description) {
		ProvideSecurityManager noExitSecurityManagerRule = createNoExitSecurityManagerRule();
		Statement statement = createStatement(base);
		return noExitSecurityManagerRule.apply(statement, description);
	}

	private ProvideSecurityManager createNoExitSecurityManagerRule() {
		NoExitSecurityManager noExitSecurityManager = new NoExitSecurityManager(
			getSecurityManager());
		return new ProvideSecurityManager(noExitSecurityManager);
	}

	private Statement createStatement(final Statement base) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					base.evaluate();
				} catch (CheckExitCalled ignored) {
				}
				checkSystemExit();
				checkAssertions();
			}
		};
	}

	private void checkSystemExit() {
		NoExitSecurityManager securityManager = (NoExitSecurityManager) getSecurityManager();
		if (securityManager.isCheckExitCalled())
			handleSystemExitWithStatus(securityManager.getStatusOfFirstCheckExitCall());
		else
			handleMissingSystemExit();
	}

	private void handleMissingSystemExit() {
		if (expectExit)
			fail("System.exit has not been called.");
	}

	private void handleSystemExitWithStatus(int status) {
		if (!expectExit)
			fail("Unexpected call of System.exit(" + status + ").");
		else if (expectedStatus != null)
			assertEquals("Wrong exit status", expectedStatus, Integer.valueOf(status));
	}

	private void checkAssertions() throws Exception {
		for (Assertion assertion : assertions)
			assertion.checkAssertion();
	}
}
