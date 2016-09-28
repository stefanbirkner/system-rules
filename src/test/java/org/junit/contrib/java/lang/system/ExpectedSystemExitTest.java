package org.junit.contrib.java.lang.system;

import org.junit.Test;
import org.junit.runners.model.Statement;

import java.security.Permission;

import static java.lang.System.getSecurityManager;
import static java.lang.System.setSecurityManager;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.contrib.java.lang.system.Executor.exceptionThrownWhenTestIsExecutedWithRule;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;
import static org.junit.contrib.java.lang.system.Statements.TEST_THAT_DOES_NOTHING;


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
	public void test_is_not_affected_by_rule_without_expectation() {
		executeTestWithRule(TEST_THAT_DOES_NOTHING, rule);
	}

	@Test
	public void test_is_successful_if_expected_exit_is_called() {
		rule.expectSystemExit();
		executeTestWithRule(new SystemExit0(), rule);
	}

	@Test
	public void test_is_successful_exit_is_called_with_expected_status_code() {
		rule.expectSystemExitWithStatus(0);
		executeTestWithRule(new SystemExit0(), rule);
	}

	@Test
	public void test_fails_if_exit_is_called_but_not_expected() {
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			new SystemExit0(), rule);
		assertThat(exception).hasMessage("Unexpected call of System.exit(0).");
	}

	@Test
	public void test_fails_if_exit_is_expected_but_not_called() {
		rule.expectSystemExit();
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			TEST_THAT_DOES_NOTHING, rule);
		assertThat(exception).hasMessage("System.exit has not been called.");
	}

	@Test
	public void test_fails_if_exit_is_called_with_wrong_status_code() {
		rule.expectSystemExitWithStatus(1);
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			new SystemExit0(), rule);
		assertThat(exception).hasMessage("Wrong exit status expected:<1> but was:<0>");
	}

	@Test
	public void test_is_successful_if_assertion_is_met_after_exit_has_been_called() {
		rule.expectSystemExit();
		rule.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() throws Exception {
				assertTrue(true);
			}
		});
		executeTestWithRule(new SystemExit0(), rule);
	}

	@Test
	public void test_fails_if_assertion_is_not_met_after_exit_has_been_called() {
		rule.expectSystemExit();
		rule.checkAssertionAfterwards(INVALID_ASSERTION);
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			new SystemExit0(), rule);
		assertThat(exception).hasMessage("Assertion failed.");
	}

	@Test
	public void test_fails_if_first_of_two_assertions_is_not_met() {
		rule.checkAssertionAfterwards(INVALID_ASSERTION);
		rule.checkAssertionAfterwards(VALID_ASSERTION);
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			TEST_THAT_DOES_NOTHING, rule);
		assertThat(exception).hasMessage("Assertion failed.");
	}

	@Test
	public void test_fails_if_second_of_two_assertions_is_not_met() {
		rule.checkAssertionAfterwards(VALID_ASSERTION);
		rule.checkAssertionAfterwards(INVALID_ASSERTION);
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			TEST_THAT_DOES_NOTHING, rule);
		assertThat(exception).hasMessage("Assertion failed.");
	}

	@Test
	public void after_test_security_manager_is_the_same_as_before() {
		SecurityManager manager = new ArbitrarySecurityManager();
		setSecurityManager(manager);
		executeTestWithRule(TEST_THAT_DOES_NOTHING, rule);
		assertThat(getSecurityManager()).isSameAs(manager);
	}

	@Test
	public void current_security_manager_is_used_for_anything_else_than_system_exit() {
		SecurityManager manager = new ArbitrarySecurityManager();
		setSecurityManager(manager);
		executeTestWithRule(new CheckContext(), rule);
	}

	@Test
	public void test_is_successful_if_expected_exit_is_called_in_a_thread() {
		rule.expectSystemExitWithStatus(ARBITRARY_EXIT_STATUS);
		rule.timeout(1000);
		executeTestWithRule(new SystemExitInSeparateThread(), rule);
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

	private static class SystemExitInSeparateThread extends Statement {
		@Override
		public void evaluate() throws Throwable {
			new Thread(new LongExecutionBeforeExitCall()).start();
		}

		private static class LongExecutionBeforeExitCall implements Runnable {
			public void run() {
				try {
					sleep(500);
					System.exit(ARBITRARY_EXIT_STATUS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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
