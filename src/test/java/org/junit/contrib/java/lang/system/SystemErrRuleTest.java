package org.junit.contrib.java.lang.system;

import static java.lang.String.format;
import static java.lang.System.setErr;
import static java.lang.System.setProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;

import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

@RunWith(Enclosed.class)
public class SystemErrRuleTest {

	@RunWith(AcceptanceTestRunner.class)
	public static class after_the_test_system_err_is_same_as_before {
		private static PrintStream originalStream;

		@BeforeClass
		public static void captureOriginalStream() {
			originalStream = System.err;
		}

		public static class TestClass {
			@Rule
			public final SystemErrRule systemErrRule = new SystemErrRule();

			@Test
			public void test() {
				System.err.print("dummy text");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(System.err).isSameAs(originalStream);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class text_is_still_written_to_system_err_if_no_log_mode_is_specified {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureErrorStream;

		@BeforeClass
		public static void replaceSystemErr() {
			originalStream = System.err;
			captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
		}

		public static class TestClass {
			@Rule
			public final SystemErrRule systemErrRule = new SystemErrRule();

			@Test
			public void test() {
				System.err.print("dummy text");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(captureErrorStream.toString()).isEqualTo("dummy text");
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setErr(originalStream);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class no_text_is_written_to_system_err_if_muted_globally {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureErrorStream;

		@BeforeClass
		public static void replaceSystemErr() {
			originalStream = System.err;
			captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
		}

		public static class TestClass {
			@Rule
			public final SystemErrRule systemErrRule = new SystemErrRule().mute();

			@Test
			public void test() {
				System.err.print("dummy text");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(captureErrorStream.toString()).isEmpty();
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setErr(originalStream);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class no_text_is_written_to_system_err_after_muted_locally {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureErrorStream;

		@BeforeClass
		public static void replaceSystemErr() {
			originalStream = System.err;
			captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
		}

		public static class TestClass {
			@Rule
			public final SystemErrRule systemErrRule = new SystemErrRule();

			@Test
			public void test() {
				System.err.print("text before muting");
				systemErrRule.mute();
				System.err.print("text after muting");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(captureErrorStream.toString())
				.isEqualTo("text before muting");
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setErr(originalStream);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class no_text_is_written_to_system_err_for_successful_test_if_muted_globally_for_successful_tests {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureErrorStream;

		@BeforeClass
		public static void replaceSystemErr() {
			originalStream = System.err;
			captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
		}

		public static class TestClass {
			@Rule
			public final SystemErrRule systemErrRule = new SystemErrRule()
				.muteForSuccessfulTests();

			@Test
			public void test() {
				System.err.print("dummy text");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(captureErrorStream.toString()).isEmpty();
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setErr(originalStream);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class text_is_written_to_system_err_for_failing_test_if_muted_globally_for_successful_tests {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureErrorStream;

		@BeforeClass
		public static void replaceSystemErr() {
			originalStream = System.err;
			captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
		}

		public static class TestClass {
			@Rule
			public final SystemErrRule systemErrRule = new SystemErrRule()
				.muteForSuccessfulTests();

			@Test
			public void test() {
				System.err.print("dummy text");
				fail();
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(captureErrorStream.toString()).isEqualTo("dummy text");
		}

		public static void expectFailure(Failure failure) {
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setErr(originalStream);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class no_text_is_written_to_system_err_for_successful_test_if_muted_locally_for_successful_tests {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureErrorStream;

		@BeforeClass
		public static void replaceSystemErr() {
			originalStream = System.err;
			captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
		}

		public static class TestClass {
			@Rule
			public final SystemErrRule systemErrRule = new SystemErrRule();

			@Test
			public void test() {
				systemErrRule.muteForSuccessfulTests();
				System.err.print("dummy text");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(captureErrorStream.toString()).isEmpty();
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setErr(originalStream);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class text_is_written_to_system_err_for_failing_test_if_muted_locally_for_successful_tests {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureErrorStream;

		@BeforeClass
		public static void replaceSystemErr() {
			originalStream = System.err;
			captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
		}

		public static class TestClass {
			@Rule
			public final SystemErrRule systemErrRule = new SystemErrRule();

			@Test
			public void test() {
				systemErrRule.muteForSuccessfulTests();
				System.err.print("dummy text");
				fail();
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(captureErrorStream.toString()).isEqualTo("dummy text");
		}

		public static void expectFailure(Failure failure) {
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setErr(originalStream);
		}
	}

	public static class no_text_is_logged_by_default {
		@Rule
		public final SystemErrRule systemErrRule = new SystemErrRule();

		@Test
		public void test() {
			System.err.print("dummy text");
			assertThat(systemErrRule.getLog()).isEmpty();
		}
	}

	public static class text_is_logged_if_log_has_been_enabled_globally {
		@Rule
		public final SystemErrRule systemErrRule = new SystemErrRule()
			.enableLog();

		@Test
		public void test() {
			System.err.print("dummy text");
			assertThat(systemErrRule.getLog()).isEqualTo("dummy text");
		}
	}

	public static class text_is_logged_after_log_has_been_enabled_locally {
		@Rule
		public final SystemErrRule systemErrRule = new SystemErrRule();

		@Test
		public void test() {
			System.err.print("text before enabling log");
			systemErrRule.enableLog();
			System.err.print("text after enabling log");
			assertThat(systemErrRule.getLog())
				.isEqualTo("text after enabling log");
		}
	}

	public static class log_contains_only_text_that_has_been_written_after_log_was_cleared {
		@Rule
		public final SystemErrRule systemErrRule = new SystemErrRule()
			.enableLog();

		@Test
		public void test() {
			System.err.print("text before clearing");
			systemErrRule.clearLog();
			System.err.print("text after clearing");
			assertThat(systemErrRule.getLog()).isEqualTo("text after clearing");
		}
	}

	public static class text_is_logged_if_rule_is_enabled_and_muted {
		@Rule
		public final SystemErrRule systemErrRule = new SystemErrRule()
			.enableLog()
			.mute();

		@Test
		public void test() {
			System.err.print("dummy text");
			assertThat(systemErrRule.getLog()).isEqualTo("dummy text");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class log_is_provided_with_new_line_characters_only_if_requested {
		@ClassRule
		public static final RestoreSystemProperties RESTORE_SYSTEM_PROPERTIES
			= new RestoreSystemProperties();

		@BeforeClass
		public static void useWindowsLineSeparator() {
			setProperty("line.separator", "\r\n");
		}

		public static class TestClass {
			@Rule
			public final SystemErrRule systemErrRule = new SystemErrRule()
				.enableLog();

			@Test
			public void test() {
				System.err.print(format("dummy%ntext%n"));
				assertThat(systemErrRule.getLogWithNormalizedLineSeparator())
					.isEqualTo("dummy\ntext\n");
			}
		}

		public static void verifyResult(Collection<Failure> failures) {
			assertThat(failures).isEmpty();
		}
	}
}
