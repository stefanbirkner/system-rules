package org.junit.contrib.java.lang.system;

import org.junit.After;
import org.junit.Test;
import org.junit.runners.model.Statement;

import java.io.PrintStream;
import java.util.Locale;

import static java.lang.System.err;
import static java.lang.System.getProperty;
import static java.lang.System.setErr;
import static java.util.Locale.CANADA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Executor.exceptionThrownWhenTestIsExecutedWithRule;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;
import static org.junit.contrib.java.lang.system.Statements.SUCCESSFUL_TEST;

public class DisallowWriteToSystemErrTest {
	private static final Locale DUMMY_LOCALE = CANADA;
	private final PrintStream orginalErr = err;

	@After
	public void restoreSystemErr() {
		setErr(orginalErr);
	}

	@Test
	public void test_is_successful_if_it_does_not_write_to_System_err() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		executeTestWithRule(SUCCESSFUL_TEST, disallowWrite);
	}

	@Test
	public void test_fails_if_it_tries_to_append_a_text_to_System_err() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.append("dummy text");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'd' although this is not allowed.");

	}

	@Test
	public void test_fails_if_it_tries_to_append_a_character_to_System_err() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.append('x');
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'x' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_tries_to_append_a_sub_sequence_of_a_text_to_System_err() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.append("dummy text", 2, 3);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'm' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_format_with_a_Locale() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.format(
					DUMMY_LOCALE, "%s, %s", "first dummy", "second dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'f' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_format_without_a_Locale() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.format("%s, %s", "first dummy", "second dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'f' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_print_with_a_boolean() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.print(true);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 't' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_print_with_a_char() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.print('a');
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'a' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_print_with_an_array_of_chars() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.print(new char[] {'d', 'u', 'm', 'm', 'y'});
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'd' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_print_with_a_double() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.print(1d);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_print_with_a_float() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.print(1f);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_print_with_an_int() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.print(1);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_print_with_a_long() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.print(1L);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_print_with_an_object() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.print(new Object());
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'j' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_print_with_a_string() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.print("dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'd' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_printf_with_a_localized_formatted_text() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.printf(
					DUMMY_LOCALE, "%s, %s", "first dummy", "second dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'f' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_printf_with_a_formatted_text() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.printf("%s, %s", "first dummy", "second dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'f' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_println_on_System_err() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.println();
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '"
				+ getProperty("line.separator").substring(0, 1)
				+ "' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_println_with_a_boolean() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.println(true);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 't' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_println_with_a_char() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.println('a');
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'a' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_println_with_an_array_of_chars() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.println(new char[] {'d', 'u', 'm', 'm', 'y'});
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'd' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_println_with_a_double() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.println(1d);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_println_with_a_float() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.println(1f);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_println_with_an_int() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.println(1);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_println_with_a_long() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.println(1L);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_println_with_an_object() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.println(new Object());
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'j' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_err_println_with_a_string() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.println("dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'd' although this is not allowed.");
	}

	@Test
	public void after_the_test_System_err_is_same_as_before() {
		DisallowWriteToSystemErr disallowWrite = new DisallowWriteToSystemErr();
		executeTestWithRule(SUCCESSFUL_TEST, disallowWrite);
		assertThat(err).isSameAs(orginalErr);
	}
}
