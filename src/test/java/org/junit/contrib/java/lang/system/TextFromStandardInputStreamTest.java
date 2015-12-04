package org.junit.contrib.java.lang.system;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.lang.System.getProperty;
import static java.lang.System.in;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Executor.exceptionThrownWhenTestIsExecutedWithRule;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;
import static org.junit.contrib.java.lang.system.Statements.SUCCESSFUL_TEST;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runners.model.Statement;

public class TextFromStandardInputStreamTest {
	private static final byte[] DUMMY_ARRAY = new byte[1024];
	private static final int VALID_OFFSET = 2;
	private static final int VALID_READ_LENGTH = 100;
	private static final IOException DUMMY_IO_EXCEPTION = new IOException();
	private static final RuntimeException DUMMY_RUNTIME_EXCEPTION = new RuntimeException();
	private static final com.github.stefanbirkner.fishbowl.Statement READ_NEXT_BYTE
		= new com.github.stefanbirkner.fishbowl.Statement() {
			public void evaluate() throws Throwable {
				System.in.read();
			}
		};

	@Rule
	public final Timeout timeout = new Timeout(1000);

	private final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

	@BeforeClass
	public static void checkArrayConstants() {
		assertThat(VALID_OFFSET).isBetween(0, DUMMY_ARRAY.length);
		assertThat(VALID_READ_LENGTH)
			.isBetween(0, DUMMY_ARRAY.length - VALID_OFFSET);
	}

	@Test
	public void provided_text_is_available_from_system_in() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				systemInMock.provideText("arbitrary text");
				Scanner scanner = new Scanner(in);
				String textFromSystemIn = scanner.nextLine();
				assertThat(textFromSystemIn).isEqualTo("arbitrary text");
			}
		}, systemInMock);
	}

	@Test
	public void specified_texts_are_available_from_system_in() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				String lineSeparator = getProperty("line.separator");
				systemInMock.provideText("first text" + lineSeparator,
					"second text" + lineSeparator);
				Scanner firstScanner = new Scanner(in);
				firstScanner.nextLine();
				Scanner secondScanner = new Scanner(in);
				String textFromSystemIn = secondScanner.nextLine();
				assertThat(textFromSystemIn).isEqualTo("second text");
			}
		}, systemInMock);
	}

	@Test
	public void no_text_is_available_from_system_in_if_no_text_has_been_provided() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.provideText();
				int character = in.read();
				assertThat(character).isEqualTo(-1);
			}
		}, systemInMock);
	}

	@Test
	public void specified_lines_are_available_from_system_in() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				systemInMock.provideLines("first text", "second text");
				Scanner firstScanner = new Scanner(in);
				firstScanner.nextLine();
				Scanner secondScanner = new Scanner(in);
				String textFromSystemIn = secondScanner.nextLine();
				assertThat(textFromSystemIn).isEqualTo("second text");
			}
		}, systemInMock);
	}

	@Test
	public void no_text_is_available_from_system_in_if_no_line_has_been_provided() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.provideLines();
				int character = in.read();
				assertThat(character).isEqualTo(-1);
			}
		}, systemInMock);
	}

	@Test
	public void system_in_provides_specified_text_and_throws_requested_IOException_afterwards() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.provideText("arbitrary text");
				systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
				assertSystemInProvidesText("arbitrary text");
				Throwable exception = exceptionThrownBy(READ_NEXT_BYTE);
				assertThat(exception).isSameAs(DUMMY_IO_EXCEPTION);
			}
		}, systemInMock);
	}

	@Test
	public void system_in_throws_requested_IOException_on_first_read_if_no_text_has_been_specified() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
				Throwable exception = exceptionThrownBy(READ_NEXT_BYTE);
				assertThat(exception).isSameAs(DUMMY_IO_EXCEPTION);
			}
		}, systemInMock);
	}

	@Test
	public void system_in_provides_specified_text_and_throws_requested_RuntimeException_afterwards() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.provideText("arbitrary text");
				systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
				assertSystemInProvidesText("arbitrary text");
				Throwable exception = exceptionThrownBy(READ_NEXT_BYTE);
				assertThat(exception).isSameAs(DUMMY_RUNTIME_EXCEPTION);
			}
		}, systemInMock);
	}

	@Test
	public void system_in_throws_requested_RuntimeException_on_first_read_if_no_text_has_been_specified() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
				Throwable exception = exceptionThrownBy(READ_NEXT_BYTE);
				assertThat(exception).isSameAs(DUMMY_RUNTIME_EXCEPTION);
			}
		}, systemInMock);
	}

	@Test
	public void an_IOException_cannot_be_requested_if_a_RuntimeException_has_already_been_requested() {
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			new Statement() {
				@Override
				public void evaluate() throws Throwable {
					systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
					systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
				}
			}, systemInMock);
		assertThat(exception)
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("You cannot call throwExceptionOnInputEnd(IOException)"
				+ " because throwExceptionOnInputEnd(RuntimeException) has"
				+ " already been called.");
	}

	@Test
	public void a_RuntimeException_cannot_be_requested_if_an_IOException_has_already_been_requested() {
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			new Statement() {
				@Override
				public void evaluate() throws Throwable {
					systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
					systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
				}
			}, systemInMock);
		assertThat(exception)
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("You cannot call"
				+ " throwExceptionOnInputEnd(RuntimeException) because"
				+ " throwExceptionOnInputEnd(IOException) has already been"
				+ " called.");
	}

	@Test
	public void after_the_test_system_in_is_same_as_before() {
		InputStream originalSystemIn = in;
		executeTestWithRule(SUCCESSFUL_TEST, systemInMock);
		assertThat(in).isSameAs(originalSystemIn);
	}

	@Test
	public void system_in_throws_NullPointerException_when_read_is_called_with_null_array() {
		//this is default behaviour of an InputStream according to its JavaDoc
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			new Statement() {
				@Override
				public void evaluate() throws Throwable {
					System.in.read(null);
				}
			}, systemInMock);
		assertThat(exception).isInstanceOf(NullPointerException.class);
	}

	@Test
	public void system_in_throws_IndexOutOfBoundsException_when_read_is_called_with_negative_offset() {
		//this is default behaviour of an InputStream according to its JavaDoc
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			new Statement() {
				@Override
				public void evaluate() throws Throwable {
					System.in.read(DUMMY_ARRAY, -1, VALID_READ_LENGTH);
				}
			}, systemInMock);
		assertThat(exception).isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	public void system_in_throws_IndexOutOfBoundsException_when_read_is_called_with_negative_length() {
		//this is default behaviour of an InputStream according to its JavaDoc
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			new Statement() {
				@Override
				public void evaluate() throws Throwable {
					System.in.read(DUMMY_ARRAY, VALID_OFFSET, -1);
				}
			}, systemInMock);
		assertThat(exception).isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	public void system_in_throws_IndexOutOfBoundsException_when_read_is_called_with_oversized_length() {
		//this is default behaviour of an InputStream according to its JavaDoc
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(
			new Statement() {
				@Override
				public void evaluate() throws Throwable {
					int oversizedLength = DUMMY_ARRAY.length - VALID_OFFSET + 1;
					System.in.read(DUMMY_ARRAY, VALID_OFFSET, oversizedLength);
				}
			}, systemInMock);
		assertThat(exception).isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	public void system_in_reads_zero_bytes_even_if_mock_should_throw_IOException_on_input_end() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
				int numBytesRead = System.in.read(DUMMY_ARRAY, VALID_OFFSET, 0);
				assertThat(numBytesRead).isZero();
			}
		}, systemInMock);
	}

	@Test
	public void system_in_reads_zero_bytes_even_if_mock_should_throw_RuntimeException_on_input_end() {
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
				int numBytesRead = System.in.read(DUMMY_ARRAY, VALID_OFFSET, 0);
				assertThat(numBytesRead).isZero();
			}
		}, systemInMock);
	}

	@Test
	public void system_in_read_bytes_throws_specified_IOException_on_input_end() {
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
				System.in.read(DUMMY_ARRAY, VALID_OFFSET, VALID_READ_LENGTH);
			}
		}, systemInMock);
		assertThat(exception).isSameAs(DUMMY_IO_EXCEPTION);
	}

	@Test
	public void system_in_read_bytes_throws_specified_RuntimeException_on_input_end() {
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
				System.in.read(DUMMY_ARRAY, VALID_OFFSET, VALID_READ_LENGTH);
			}
		}, systemInMock);
		assertThat(exception).isSameAs(DUMMY_RUNTIME_EXCEPTION);
	}

	private void assertSystemInProvidesText(String text) throws IOException {
		for (char c : text.toCharArray())
			assertThat((char) System.in.read()).isSameAs(c);
	}
}
