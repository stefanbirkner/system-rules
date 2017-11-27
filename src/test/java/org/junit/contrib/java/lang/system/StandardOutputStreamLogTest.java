package org.junit.contrib.java.lang.system;

import static java.lang.System.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

@RunWith(Enclosed.class)
public class StandardOutputStreamLogTest {

	public static class log_contains_text_that_has_been_written_to_system_out {
		@Rule
		public final StandardOutputStreamLog log = new StandardOutputStreamLog();

		@Test
		public void test() {
			System.out.print("dummy text");
			assertThat(log.getLog()).isEqualTo("dummy text");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class after_the_test_system_out_is_same_as_before {
		private static PrintStream originalStream;

		@BeforeClass
		public static void captureOriginalStream() {
			originalStream = System.out;
		}

		public static class TestClass {
			@Rule
			public final StandardOutputStreamLog log = new StandardOutputStreamLog();

			@Test
			public void test() {
				System.out.print("dummy text");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(originalStream).isSameAs(System.out);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class text_is_still_written_to_system_out_if_no_log_mode_is_specified {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureOutputStream;

		@BeforeClass
		public static void replaceSystemOut() {
			originalStream = System.out;
			captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
		}

		public static class TestClass {
			@Rule
			public final StandardOutputStreamLog log = new StandardOutputStreamLog();

			@Test
			public void test() {
				System.out.print("dummy text");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(captureOutputStream.toString()).isEqualTo("dummy text");
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setOut(originalStream);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class no_text_is_written_to_system_out_if_log_mode_is_log_only {
		private static PrintStream originalStream;
		private static ByteArrayOutputStream captureOutputStream;

		@BeforeClass
		public static void replaceSystemOut() {
			originalStream = System.out;
			captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
		}

		public static class TestClass {
			@Rule
			public final StandardOutputStreamLog log = new StandardOutputStreamLog(
				LogMode.LOG_ONLY);

			@Test
			public void test() {
				System.out.print("dummy text");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(captureOutputStream.toString()).isEmpty();
		}

		@AfterClass
		public static void restoreOriginalStream() {
			setOut(originalStream);
		}
	}

	public static class log_contains_only_text_that_has_been_written_after_log_was_cleared {
		@Rule
		public final StandardOutputStreamLog log = new StandardOutputStreamLog();

		@Test
		public void test() {
			System.out.print("text before clearing");
			log.clear();
			System.out.print("text after clearing");
			assertThat(log.getLog()).isEqualTo("text after clearing");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class rule_cannot_be_created_without_log_mode {
		public static class TestClass {
			@Rule
			public final StandardOutputStreamLog log = new StandardOutputStreamLog(
				null);

			@Test
			public void test() {
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getException())
				.isInstanceOf(NullPointerException.class)
				.hasMessage("The LogMode is missing.");
			assertThat(failure.getMessage())
				.isEqualTo("The LogMode is missing.");
		}
	}
}
