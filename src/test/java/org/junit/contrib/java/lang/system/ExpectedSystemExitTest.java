package org.junit.contrib.java.lang.system;

import static java.lang.System.*;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.Permission;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;


@RunWith(Enclosed.class)
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

	public static class test_is_not_affected_by_rule_without_expectation {
		@Rule
		public final ExpectedSystemExit exit = ExpectedSystemExit.none();

		@Test
		public void test() {
		}
	}

	public static class test_is_successful_if_expected_exit_is_called {
		@Rule
		public final ExpectedSystemExit exit = ExpectedSystemExit.none();

		@Test
		public void test() {
			exit.expectSystemExit();
			System.exit(0);
		}
	}

	public static class test_is_successful_exit_is_called_with_expected_status_code {
		@Rule
		public final ExpectedSystemExit exit = ExpectedSystemExit.none();

		@Test
		public void test() {
			exit.expectSystemExitWithStatus(0);
			System.exit(0);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_exit_is_called_but_not_expected {
		public static class TestClass {
			@Rule
			public final ExpectedSystemExit exit = ExpectedSystemExit.none();

			@Test
			public void test() {
				System.exit(0);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Unexpected call of System.exit(0).");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_exit_is_expected_but_not_called {
		public static class TestClass {
			@Rule
			public final ExpectedSystemExit exit = ExpectedSystemExit.none();

			@Test
			public void test() {
				exit.expectSystemExit();
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("System.exit has not been called.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_exit_is_called_with_wrong_status_code {
		public static class TestClass {
			@Rule
			public final ExpectedSystemExit exit = ExpectedSystemExit.none();

			@Test
			public void test() {
				exit.expectSystemExitWithStatus(1);
				System.exit(0);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Wrong exit status expected:<1> but was:<0>");
		}
	}

	public static class test_is_successful_if_assertion_is_met_after_exit_has_been_called {
		@Rule
		public final ExpectedSystemExit exit = ExpectedSystemExit.none();

		@Test
		public void test() {
			exit.expectSystemExit();
			exit.checkAssertionAfterwards(new Assertion() {
				public void checkAssertion() throws Exception {
					assertTrue(true);
				}
			});
			System.exit(0);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_assertion_is_not_met_after_exit_has_been_called {
		public static class TestClass {
			@Rule
			public final ExpectedSystemExit exit = ExpectedSystemExit.none();

			@Test
			public void test() {
				exit.expectSystemExit();
				exit.checkAssertionAfterwards(INVALID_ASSERTION);
				System.exit(0);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Assertion failed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_first_of_two_assertions_is_not_met {
		public static class TestClass {
			@Rule
			public final ExpectedSystemExit exit = ExpectedSystemExit.none();

			@Test
			public void test() {
				exit.checkAssertionAfterwards(INVALID_ASSERTION);
				exit.checkAssertionAfterwards(VALID_ASSERTION);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Assertion failed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_second_of_two_assertions_is_not_met {
		public static class TestClass {
			@Rule
			public final ExpectedSystemExit exit = ExpectedSystemExit.none();

			@Test
			public void test() {
				exit.checkAssertionAfterwards(VALID_ASSERTION);
				exit.checkAssertionAfterwards(INVALID_ASSERTION);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Assertion failed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class after_test_security_manager_is_the_same_as_before {
		private static final SecurityManager MANAGER = new ArbitrarySecurityManager();

		@BeforeClass
		public static void setFixedSecurityManager() {
			setSecurityManager(MANAGER);
		}

		public static class TestClass {
			@Rule
			public final ExpectedSystemExit exit = ExpectedSystemExit.none();

			@Test
			public void test() {
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(getSecurityManager()).isSameAs(MANAGER);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class current_security_manager_is_used_for_anything_else_than_system_exit {
		private static final SecurityManager MANAGER = new ArbitrarySecurityManager();

		@BeforeClass
		public static void setFixedSecurityManager() {
			setSecurityManager(MANAGER);
		}

		public static class TestClass {
			@Rule
			public final ExpectedSystemExit exit = ExpectedSystemExit.none();

			@Test
			public void test() {
				assertEquals(ARBITRARY_CONTEXT, getSecurityManager().getSecurityContext());
			}
		}

		public static void verifyResult(Collection<Failure> failures) {
			assertThat(failures).isEmpty();
		}
	}

	public static class test_is_successful_if_expected_exit_is_called_in_a_thread {
		@Rule
		public final ExpectedSystemExit exit = ExpectedSystemExit.none();

		@Test
		public void test() throws Throwable {
			exit.expectSystemExitWithStatus(ARBITRARY_EXIT_STATUS);
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
