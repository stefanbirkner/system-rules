package org.junit.contrib.java.lang.system;

import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.io.PrintStream;
import java.util.Locale;

import static java.lang.System.*;
import static java.util.Locale.CANADA;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class DisallowWriteToSystemErrTest {
	private static final Locale DUMMY_LOCALE = CANADA;

	public static class test_is_successful_if_it_does_not_write_to_System_err {
		@Rule
		public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

		@Test
		public void test() {
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_tries_to_append_a_text_to_System_err {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.append("dummy text");
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'd' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_tries_to_append_a_character_to_System_err {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.append('x');
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'x' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_tries_to_append_a_sub_sequence_of_a_text_to_System_err {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.append("dummy text", 2, 3);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'm' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_format_with_a_Locale {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.format(DUMMY_LOCALE, "%s, %s", "first dummy", "second dummy");
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'f' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_format_without_a_Locale {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.format("%s, %s", "first dummy", "second dummy");
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'f' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_print_with_a_boolean {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.print(true);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 't' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_print_with_a_char {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.print('a');
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'a' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_print_with_an_array_of_chars {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.print(new char[]{'d', 'u', 'm', 'm', 'y'});
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'd' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_print_with_a_double {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.print(1d);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_print_with_a_float {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.print(1f);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_print_with_an_int {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.print(1);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_print_with_a_long {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.print(1L);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_print_with_an_object {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.print(new Object());
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'j' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_print_with_a_string {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.print("dummy");
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'd' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_printf_with_a_localized_formatted_text {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.printf(DUMMY_LOCALE, "%s, %s", "first dummy", "second dummy");
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'f' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_printf_with_a_formatted_text {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.printf("%s, %s", "first dummy", "second dummy");
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'f' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_println_on_System_err {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.println();
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '"
					+ getProperty("line.separator").substring(0, 1)
					+ "' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_println_with_a_boolean {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.println(true);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 't' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_println_with_a_char {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.println('a');
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'a' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_println_with_an_array_of_chars {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.println(new char[]{'d', 'u', 'm', 'm', 'y'});
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'd' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_println_with_a_double {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.println(1d);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_println_with_a_float {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.println(1f);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_println_with_an_int {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.println(1);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_println_with_a_long {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.println(1L);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write '1' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_println_with_an_object {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.println(new Object());
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'j' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class test_fails_if_it_calls_System_err_println_with_a_string {
		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
				System.err.println("dummy");
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("Tried to write 'd' although this is not allowed.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class after_the_test_System_err_is_same_as_before {
		private static PrintStream originalErr;

		@BeforeClass
		public static void captureSystemErr() {
			originalErr = err;
		}

		public static class TestClass {
			@Rule
			public final DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();

			@Test
			public void test() {
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(err).isSameAs(originalErr);
		}
	}
}
