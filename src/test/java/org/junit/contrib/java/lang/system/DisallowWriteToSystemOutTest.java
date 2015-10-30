package org.junit.contrib.java.lang.system;

import org.junit.After;
import org.junit.Test;
import org.junit.runners.model.Statement;

import java.io.PrintStream;
import java.util.Locale;

import static java.lang.System.out;
import static java.lang.System.setOut;
import static java.util.Locale.CANADA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Executor.exceptionThrownWhenTestIsExecutedWithRule;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;
import static org.junit.contrib.java.lang.system.Statements.SUCCESSFUL_TEST;

public class DisallowWriteToSystemOutTest {
	private static final Locale DUMMY_LOCALE = CANADA;
	private final PrintStream originalOut = out;

	@After
	public void restoreSystemOut() {
		setOut(originalOut);
	}

	@Test
	public void test_is_successful_if_it_does_not_write_to_System_out() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		executeTestWithRule(SUCCESSFUL_TEST, disallowWrite);
	}

	@Test
	public void test_fails_if_it_tries_to_append_a_text_to_System_out() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.append("dummy text");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'd' although this is not allowed.");

	}

	@Test
	public void test_fails_if_it_tries_to_append_a_character_to_System_out() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.append('x');
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'x' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_tries_to_append_a_sub_sequence_of_a_text_to_System_out() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.append("dummy text", 2, 3);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'm' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_format_with_a_Locale() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.format(
					DUMMY_LOCALE, "%s, %s", "first dummy", "second dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'f' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_format_without_a_Locale() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.format("%s, %s", "first dummy", "second dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'f' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_print_with_a_boolean() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.print(true);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 't' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_print_with_a_char() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.print('a');
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'a' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_print_with_an_array_of_chars() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.print(new char[] {'d', 'u', 'm', 'm', 'y'});
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'd' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_print_with_a_double() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.print(1d);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_print_with_a_float() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.print(1f);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_print_with_an_int() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.print(1);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_print_with_a_long() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.print(1L);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_print_with_an_object() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.print(new Object());
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'j' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_print_with_a_string() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.print("dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'd' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_printf_with_a_localized_formatted_text() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.printf(
					DUMMY_LOCALE, "%s, %s", "first dummy", "second dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'f' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_printf_with_a_formatted_text() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.printf("%s, %s", "first dummy", "second dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'f' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_println_on_System_out() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.println();
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '\n' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_println_with_a_boolean() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.println(true);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 't' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_println_with_a_char() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.println('a');
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'a' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_println_with_an_array_of_chars() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.println(new char[] {'d', 'u', 'm', 'm', 'y'});
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'd' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_println_with_a_double() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.println(1d);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_println_with_a_float() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.println(1f);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_println_with_an_int() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.println(1);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_println_with_a_long() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.println(1L);
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write '1' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_println_with_an_object() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.println(new Object());
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'j' although this is not allowed.");
	}

	@Test
	public void test_fails_if_it_calls_System_out_println_with_a_string() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		Throwable error = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.println("dummy");
			}
		}, disallowWrite);
		assertThat(error)
			.isInstanceOf(AssertionError.class)
			.hasMessage("Tried to write 'd' although this is not allowed.");
	}

	@Test
	public void after_the_test_System_out_is_same_as_before() {
		DisallowWriteToSystemOut disallowWrite = new DisallowWriteToSystemOut();
		executeTestWithRule(SUCCESSFUL_TEST, disallowWrite);
		assertThat(out).isSameAs(originalOut);
	}
}
