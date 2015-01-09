package org.junit.contrib.java.lang.system;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.lang.System.getSecurityManager;
import static java.lang.System.setSecurityManager;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.contrib.java.lang.system.Statements.TEST_THAT_DOES_NOTHING;

import java.security.Permission;

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
	public void test_is_not_affected_by_rule_without_expectation() throws Throwable {
		executeRuleWithoutExitCall();
	}

	@Test
	public void test_is_successful_if_expected_exit_is_called() throws Throwable {
		rule.expectSystemExit();
		executeRuleWithExitStatus0();
	}

	@Test
	public void test_is_successful_exit_is_called_with_expected_status_code() throws Throwable {
		rule.expectSystemExitWithStatus(0);
		executeRuleWithExitStatus0();
	}

	@Test
	public void test_fails_if_exit_is_called_but_not_expected() throws Throwable {
		Throwable exception = exceptionThrownByRuleForStatement(new SystemExit0());
		assertThat(exception).hasMessage("Unexpected call of System.exit(0).");
	}

	@Test
	public void test_fails_if_exit_is_expected_but_not_called() throws Throwable {
		rule.expectSystemExit();
		Throwable exception = exceptionThrownByRuleForStatement(TEST_THAT_DOES_NOTHING);
		assertThat(exception).hasMessage("System.exit has not been called.");
	}

	@Test
	public void test_fails_if_exit_is_called_with_wrong_status_code() throws Throwable {
		rule.expectSystemExitWithStatus(1);
		Throwable exception = exceptionThrownByRuleForStatement(new SystemExit0());
		assertThat(exception).hasMessage("Wrong exit status expected:<1> but was:<0>");
	}

	@Test
	public void test_is_successful_if_assertion_is_met_after_exit_has_been_called() throws Throwable {
		rule.expectSystemExit();
		rule.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() throws Exception {
				assertTrue(true);
			}
		});
		executeRuleWithExitStatus0();
	}

	@Test
	public void test_fails_if_assertion_is_not_met_after_exit_has_been_called() throws Throwable {
		rule.expectSystemExit();
		rule.checkAssertionAfterwards(INVALID_ASSERTION);
		Throwable exception = exceptionThrownByRuleForStatement(new SystemExit0());
		assertThat(exception).hasMessage("Assertion failed.");
	}

	@Test
	public void test_fails_if_first_of_two_assertions_is_not_met() throws Throwable {
		rule.checkAssertionAfterwards(INVALID_ASSERTION);
		rule.checkAssertionAfterwards(VALID_ASSERTION);
		Throwable exception = exceptionThrownByRuleForStatement(TEST_THAT_DOES_NOTHING);
		assertThat(exception).hasMessage("Assertion failed.");
	}

	@Test
	public void test_fails_if_second_of_two_assertions_is_not_met() throws Throwable {
		rule.checkAssertionAfterwards(VALID_ASSERTION);
		rule.checkAssertionAfterwards(INVALID_ASSERTION);
		Throwable exception = exceptionThrownByRuleForStatement(TEST_THAT_DOES_NOTHING);
		assertThat(exception).hasMessage("Assertion failed.");
	}

	@Test
	public void after_test_security_manager_is_the_same_as_before()
			throws Throwable {
		SecurityManager manager = new ArbitrarySecurityManager();
		setSecurityManager(manager);
		executeRuleWithoutExitCall();
		assertThat(getSecurityManager()).isSameAs(manager);
	}

	@Test
	public void current_security_manager_is_used_for_anything_else_than_system_exit()
		throws Throwable {
		SecurityManager manager = new ArbitrarySecurityManager();
		setSecurityManager(manager);
		executeRuleWithStatement(new CheckContext());
	}

	@Test
	public void test_is_successful_if_expected_exit_is_called_in_a_thread()
		throws Throwable {
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
		executeRuleWithStatement(TEST_THAT_DOES_NOTHING);
	}

	private void executeRuleWithExitStatus0() throws Throwable {
		executeRuleWithStatement(new SystemExit0());
	}

	private void executeRuleWithStatement(Statement statement) throws Throwable {
		rule.apply(statement, null).evaluate();
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
