package org.junit.contrib.java.lang.system;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.lang.System.getSecurityManager;
import static java.lang.System.setSecurityManager;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.Permission;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runners.model.Statement;


public class ExpectedSystemExitTest {
	private static final Object ARBITRARY_CONTEXT = new Object();
	private static final int ARBITRARY_EXIT_STATUS = 216843;
	private static final Assertion INVALID_ASSERTION = new Assertion() {
		public void checkAssertion() throws Exception {
			fail("Assertion failed.");
		}
	};
	private static final Assertion VALID_ASSERTION = new Assertion() {
		public void checkAssertion() throws Exception {
		}
	};

	private final ExpectedSystemExit rule = ExpectedSystemExit.none();

	@Test
	public void succeedWithoutExit() throws Throwable {
		executeRuleWithoutExitCall();
	}

	@Test
	public void succeedOnExitWithArbitraryStatusCode() throws Throwable {
		rule.expectSystemExit();
		executeRuleWithExitStatus0();
	}

	@Test
	public void succeedOnExitWithSelectedStatusCode() throws Throwable {
		rule.expectSystemExitWithStatus(0);
		executeRuleWithExitStatus0();
	}

	@Test
	public void failForUnexpectedSystemExit() throws Throwable {
		Throwable exception = exceptionThrownByRuleForStatement(new SystemExit0());
		assertThat(exception, hasMessage("Unexpected call of System.exit(0)."));
	}

	@Test
	public void failBecauseOfMissingSystemExitCall() throws Throwable {
		rule.expectSystemExit();
		Throwable exception = exceptionThrownByRuleForStatement(new EmptyStatement());
		assertThat(exception, hasMessage("System.exit has not been called."));
	}

	@Test
	public void failForWrongStatus() throws Throwable {
		rule.expectSystemExitWithStatus(1);
		Throwable exception = exceptionThrownByRuleForStatement(new SystemExit0());
		assertThat(exception, hasMessage("Wrong exit status expected:<1> but was:<0>"));
	}

	@Test
	public void succeedOnExitWithValidAssertion() throws Throwable {
		rule.expectSystemExit();
		rule.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() throws Exception {
				assertTrue(true);
			}
		});
		executeRuleWithExitStatus0();
	}

	@Test
	public void failsOnExitWithInvalidAssertion() throws Throwable {
		rule.expectSystemExit();
		rule.checkAssertionAfterwards(INVALID_ASSERTION);
		Throwable exception = exceptionThrownByRuleForStatement(new SystemExit0());
		assertThat(exception, hasMessage("Assertion failed."));
	}

	@Test
	public void failsOnFirstOfTwoAssertions() throws Throwable {
		rule.checkAssertionAfterwards(INVALID_ASSERTION);
		rule.checkAssertionAfterwards(VALID_ASSERTION);
		Throwable exception = exceptionThrownByRuleForStatement(new EmptyStatement());
		assertThat(exception, hasMessage("Assertion failed."));
	}

	@Test
	public void failsOnSecondOfTwoAssertions() throws Throwable {
		rule.checkAssertionAfterwards(VALID_ASSERTION);
		rule.checkAssertionAfterwards(INVALID_ASSERTION);
		Throwable exception = exceptionThrownByRuleForStatement(new EmptyStatement());
		assertThat(exception, hasMessage("Assertion failed."));
	}

	@Test
	public void restoreOldSecurityManager() throws Throwable {
		SecurityManager manager = new ArbitrarySecurityManager();
		setSecurityManager(manager);
		executeRuleWithoutExitCall();
		assertThat(getSecurityManager(), sameInstance(manager));
	}

	@Test
	public void delegateToOldSecurityManager() throws Throwable {
		SecurityManager manager = new ArbitrarySecurityManager();
		setSecurityManager(manager);
		executeRuleWithStatement(new CheckContext());
	}

	@Test
	public void succeedsOnExitInThread() throws Throwable {
		rule.expectSystemExitWithStatus(ARBITRARY_EXIT_STATUS);
		executeRuleWithStatement(new SystemExitInThread());
	}

	private Throwable exceptionThrownByRuleForStatement(final Statement statement) {
		return exceptionThrownBy(new com.github.stefanbirkner.fishbowl.Statement() {
			public void evaluate() throws Throwable {
				executeRuleWithStatement(statement);
			}
		});
	}

	private void executeRuleWithoutExitCall() throws Throwable {
		executeRuleWithStatement(new EmptyStatement());
	}

	private void executeRuleWithExitStatus0() throws Throwable {
		executeRuleWithStatement(new SystemExit0());
	}

	private void executeRuleWithStatement(Statement statement) throws Throwable {
		rule.apply(statement, null).evaluate();
	}

	private Matcher<Throwable> hasMessage(String message) {
		return hasProperty("message", equalTo(message));
	}

	private static class SystemExit0 extends Statement {
		@Override
		public void evaluate() throws Throwable {
			System.exit(0);
		}
	}

	private static class CheckContext extends Statement {
		@Override
		public void evaluate() throws Throwable {
			assertEquals(ARBITRARY_CONTEXT, getSecurityManager().getSecurityContext());
		}
	}

	private static class SystemExitInThread extends Statement {
		@Override
		public void evaluate() throws Throwable {
			Runnable callSystemExit = new Runnable() {
				public void run() {
					System.exit(ARBITRARY_EXIT_STATUS);
				}
			};
			Thread thread = new Thread(callSystemExit);
			thread.start();
			sleep(1000); // wait until the thread exits
		}
	}

	private static class ArbitrarySecurityManager extends SecurityManager {
		@Override
		public Object getSecurityContext() {
			return ARBITRARY_CONTEXT;
		}

		@Override
		public void checkPermission(Permission perm) {
			// allow anything.
		}
	}
}
