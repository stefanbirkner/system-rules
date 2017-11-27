package org.junit.contrib.java.lang.system;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.lang.System.getProperty;
import static java.lang.System.in;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

@RunWith(Enclosed.class)
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

	@BeforeClass
	public static void checkArrayConstants() {
		assertThat(VALID_OFFSET).isBetween(0, DUMMY_ARRAY.length);
		assertThat(VALID_READ_LENGTH)
			.isBetween(0, DUMMY_ARRAY.length - VALID_OFFSET);
	}

	public static class provided_text_is_available_from_system_in {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() {
			systemInMock.provideText("arbitrary text");
			Scanner scanner = new Scanner(in);
			String textFromSystemIn = scanner.nextLine();
			assertThat(textFromSystemIn).isEqualTo("arbitrary text");
		}
	}

	public static class specified_texts_are_available_from_system_in {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() {
			String lineSeparator = getProperty("line.separator");
			systemInMock.provideText("first text" + lineSeparator,
				"second text" + lineSeparator);
			Scanner firstScanner = new Scanner(in);
			firstScanner.nextLine();
			Scanner secondScanner = new Scanner(in);
			String textFromSystemIn = secondScanner.nextLine();
			assertThat(textFromSystemIn).isEqualTo("second text");
		}
	}

	public static class no_text_is_available_from_system_in_if_no_text_has_been_provided {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() throws Exception {
			systemInMock.provideText();
			int character = in.read();
			assertThat(character).isEqualTo(-1);
		}
	}

	public static class specified_lines_are_available_from_system_in {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() {
			systemInMock.provideLines("first text", "second text");
			Scanner firstScanner = new Scanner(in);
			firstScanner.nextLine();
			Scanner secondScanner = new Scanner(in);
			String textFromSystemIn = secondScanner.nextLine();
			assertThat(textFromSystemIn).isEqualTo("second text");
		}
	}

	public static class no_text_is_available_from_system_in_if_no_line_has_been_provided {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() throws Exception {
			systemInMock.provideLines();
			int character = in.read();
			assertThat(character).isEqualTo(-1);
		}
	}

	public static class system_in_provides_specified_text_and_throws_requested_IOException_afterwards {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() throws Exception {
			systemInMock.provideText("arbitrary text");
			systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
			assertSystemInProvidesText("arbitrary text");
			Throwable exception = exceptionThrownBy(READ_NEXT_BYTE);
			assertThat(exception).isSameAs(DUMMY_IO_EXCEPTION);
		}
	}

	public static class system_in_throws_requested_IOException_on_first_read_if_no_text_has_been_specified {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() {
			systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
			Throwable exception = exceptionThrownBy(READ_NEXT_BYTE);
			assertThat(exception).isSameAs(DUMMY_IO_EXCEPTION);
		}
	}

	public static class system_in_provides_specified_text_and_throws_requested_RuntimeException_afterwards {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() throws Exception {
			systemInMock.provideText("arbitrary text");
			systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
			assertSystemInProvidesText("arbitrary text");
			Throwable exception = exceptionThrownBy(READ_NEXT_BYTE);
			assertThat(exception).isSameAs(DUMMY_RUNTIME_EXCEPTION);
		}
	}

	public static class system_in_throws_requested_RuntimeException_on_first_read_if_no_text_has_been_specified {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() {
			systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
			Throwable exception = exceptionThrownBy(READ_NEXT_BYTE);
			assertThat(exception).isSameAs(DUMMY_RUNTIME_EXCEPTION);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class an_IOException_cannot_be_requested_if_a_RuntimeException_has_already_been_requested {
		public static class TestClass {
			@Rule
			public final TextFromStandardInputStream systemInMock
				= emptyStandardInputStream();

			@Test
			public void test() {
				systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
				systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("You cannot call throwExceptionOnInputEnd(IOException)"
					+ " because throwExceptionOnInputEnd(RuntimeException) has"
					+ " already been called.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class a_RuntimeException_cannot_be_requested_if_an_IOException_has_already_been_requested {
		public static class TestClass {
			@Rule
			public final TextFromStandardInputStream systemInMock
				= emptyStandardInputStream();

			@Test
			public void test() {
				systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
				systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getMessage())
				.isEqualTo("You cannot call"
					+ " throwExceptionOnInputEnd(RuntimeException) because"
					+ " throwExceptionOnInputEnd(IOException) has already been"
					+ " called.");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class after_the_test_system_in_is_same_as_before {
		private static InputStream originalSystemIn;

		@BeforeClass
		public static void captureSystemIn() {
			originalSystemIn = System.in;
		}

		public static class TestClass {
			@Rule
			public final TextFromStandardInputStream systemInMock
				= emptyStandardInputStream();

			@Test
			public void test() {
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(System.in).isSameAs(originalSystemIn);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	//this is default behaviour of an InputStream according to its JavaDoc
	public static class system_in_throws_NullPointerException_when_read_is_called_with_null_array {
		public static class TestClass {
			@Rule
			public final TextFromStandardInputStream systemInMock
				= emptyStandardInputStream();

			@Test
			public void test() throws Exception {
				System.in.read(null);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getException())
				.isInstanceOf(NullPointerException.class);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	//this is default behaviour of an InputStream according to its JavaDoc
	public static class system_in_throws_IndexOutOfBoundsException_when_read_is_called_with_negative_offset {
		public static class TestClass {
			@Rule
			public final TextFromStandardInputStream systemInMock
				= emptyStandardInputStream();

			@Test
			public void test() throws Exception {
				System.in.read(DUMMY_ARRAY, -1, VALID_READ_LENGTH);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getException())
				.isInstanceOf(IndexOutOfBoundsException.class);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	//this is default behaviour of an InputStream according to its JavaDoc
	public static class system_in_throws_IndexOutOfBoundsException_when_read_is_called_with_negative_length {
		public static class TestClass {
			@Rule
			public final TextFromStandardInputStream systemInMock
				= emptyStandardInputStream();

			@Test
			public void test() throws Exception {
				System.in.read(DUMMY_ARRAY, VALID_OFFSET, -1);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getException())
				.isInstanceOf(IndexOutOfBoundsException.class);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	//this is default behaviour of an InputStream according to its JavaDoc
	public static class system_in_throws_IndexOutOfBoundsException_when_read_is_called_with_oversized_length {
		public static class TestClass {
			@Rule
			public final TextFromStandardInputStream systemInMock
				= emptyStandardInputStream();

			@Test
			public void test() throws Exception {
				int oversizedLength = DUMMY_ARRAY.length - VALID_OFFSET + 1;
				System.in.read(DUMMY_ARRAY, VALID_OFFSET, oversizedLength);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getException())
				.isInstanceOf(IndexOutOfBoundsException.class);
		}
	}

	public static class system_in_reads_zero_bytes_even_if_mock_should_throw_IOException_on_input_end {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() throws Exception {
			systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
			int numBytesRead = System.in.read(DUMMY_ARRAY, VALID_OFFSET, 0);
			assertThat(numBytesRead).isZero();
		}
	}

	public static class system_in_reads_zero_bytes_even_if_mock_should_throw_RuntimeException_on_input_end {
		@Rule
		public final TextFromStandardInputStream systemInMock
			= emptyStandardInputStream();

		@Test
		public void test() throws Exception {
			systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
			int numBytesRead = System.in.read(DUMMY_ARRAY, VALID_OFFSET, 0);
			assertThat(numBytesRead).isZero();
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class system_in_read_bytes_throws_specified_IOException_on_input_end {
		public static class TestClass {
			@Rule
			public final TextFromStandardInputStream systemInMock
				= emptyStandardInputStream();

			@Test
			public void test() throws Exception {
				systemInMock.throwExceptionOnInputEnd(DUMMY_IO_EXCEPTION);
				System.in.read(DUMMY_ARRAY, VALID_OFFSET, VALID_READ_LENGTH);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getException())
				.isSameAs(DUMMY_IO_EXCEPTION);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class system_in_read_bytes_throws_specified_RuntimeException_on_input_end {
		public static class TestClass {
			@Rule
			public final TextFromStandardInputStream systemInMock
				= emptyStandardInputStream();

			@Test
			public void test() throws Exception {
				systemInMock.throwExceptionOnInputEnd(DUMMY_RUNTIME_EXCEPTION);
				System.in.read(DUMMY_ARRAY, VALID_OFFSET, VALID_READ_LENGTH);
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(failure.getException())
				.isSameAs(DUMMY_RUNTIME_EXCEPTION);
		}
	}

	private static void assertSystemInProvidesText(String text) throws IOException {
		for (char c : text.toCharArray())
			assertThat((char) System.in.read()).isSameAs(c);
	}
}
