package org.junit.contrib.java.lang.system;

import static java.lang.System.getProperty;
import static java.lang.System.in;
import static java.lang.System.setIn;
import static java.nio.charset.Charset.defaultCharset;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.junit.rules.ExternalResource;

/**
 * The {@code TextFromStandardInputStream} rule replaces {@code System.in} with
 * another {@code InputStream}, which provides an arbitrary text. The original
 * {@code System.in} is restored after the test.
 *
 * <pre>
 *   public void MyTest {
 *     &#064;Rule
 *     public final TextFromStandardInputStream systemInMock
 *       = emptyStandardInputStream();
 *
 *     &#064;Test
 *     public void readTextFromStandardInputStream() {
 *       systemInMock.provideLines("foo", "bar");
 *       Scanner scanner = new Scanner(System.in);
 *       scanner.nextLine();
 *       assertEquals("bar", scanner.nextLine());
 *     }
 *   }
 * </pre>
 *
 * <h3>Throwing Exceptions</h3>
 * <p>{@code TextFromStandardInputStream} can also simulate a {@code System.in}
 * that throws an {@code IOException} or {@code RuntimeException}. Use
 * <pre>   systemInMock.{@link #throwExceptionOnInputEnd(IOException)}</pre>
 * <p>or
 * <pre>   systemInMock.{@link #throwExceptionOnInputEnd(RuntimeException)}</pre>
 * <p>If you call {@link #provideLines(String...)} in addition then the
 * exception is thrown after the text has been read from {@code System.in}.
 */
public class TextFromStandardInputStream extends ExternalResource {
	private final SystemInMock systemInMock = new SystemInMock();
	private InputStream originalIn;

	public static TextFromStandardInputStream emptyStandardInputStream() {
		return new TextFromStandardInputStream("");
	}

	/**
	 * Create a new {@code TextFromStandardInputStream}, which provides the
	 * specified text.
	 *
	 * @param text this text is return by {@code System.in}.
	 * @deprecated use {@link #provideLines(String...)}
	 */
	@Deprecated
	public TextFromStandardInputStream(String text) {
		provideText(text);
	}

	/**
	 * Set the text that is returned by {@code System.in}. You can
	 * provide multiple texts. In that case the texts are concatenated.
	 *
	 * @param texts a list of texts.
	 * @deprecated please use {@link #provideLines(String...)}
	 */
	@Deprecated
	public void provideText(String... texts) {
		systemInMock.provideText(join(texts));
	}

	/**
	 * Set the lines that are returned by {@code System.in}.
	 * {@code System.getProperty("line.separator")} is used for the end
	 * of line.
	 *
	 * @param lines a list of lines.
	 */
	public void provideLines(String... lines) {
		systemInMock.provideText(joinLines(lines));
	}

	/**
	 * Specify an {@code IOException} that is thrown by {@code System.in}. If
	 * you call {@link #provideLines(String...)} or
	 * {@link #provideText(String...)} in addition then the exception is thrown
	 * after the text has been read from {@code System.in}.
	 *
	 * @param exception the {@code IOException} that is thrown.
	 * @see #throwExceptionOnInputEnd(RuntimeException)
	 * @throws IllegalStateException if
	 * {@link #throwExceptionOnInputEnd(RuntimeException)} has been called before.
     */
	public void throwExceptionOnInputEnd(IOException exception) {
		systemInMock.throwExceptionOnInputEnd(exception);
	}

	/**
	 * Specify a {@code RuntimeException} that is thrown by {@code System.in}.
	 * If you call {@link #provideLines(String...)} or
	 * {@link #provideText(String...)} in addition then the exception is thrown
	 * after the text has been read from {@code System.in}.
	 *
	 * @param exception the {@code RuntimeException} that is thrown.
	 * @see #throwExceptionOnInputEnd(IOException)
	 * @throws IllegalStateException if
	 * {@link #throwExceptionOnInputEnd(IOException)} has been called before.
	 */
	public void throwExceptionOnInputEnd(RuntimeException exception) {
		systemInMock.throwExceptionOnInputEnd(exception);
	}

	private String join(String[] texts) {
		StringBuilder sb = new StringBuilder();
		for (String text: texts)
			sb.append(text);
		return sb.toString();
	}

	private String joinLines(String[] lines) {
		StringBuilder sb = new StringBuilder();
		for (String line: lines)
			sb.append(line).append(getProperty("line.separator"));
		return sb.toString();
	}

	@Override
	protected void before() throws Throwable {
		originalIn = in;
		setIn(systemInMock);
	}

	@Override
	protected void after() {
		setIn(originalIn);
	}

	private static class SystemInMock extends InputStream {
		private StringReader currentReader;
		private IOException ioException;
		private RuntimeException runtimeException;

		void provideText(String text) {
			currentReader = new StringReader(text);
		}

		void throwExceptionOnInputEnd(IOException exception) {
			if (runtimeException != null)
				throw new IllegalStateException("You cannot call"
					+ " throwExceptionOnInputEnd(IOException) because"
					+ " throwExceptionOnInputEnd(RuntimeException) has already"
					+ " been called.");
			ioException = exception;
		}

		void throwExceptionOnInputEnd(RuntimeException exception) {
			if (ioException != null)
				throw new IllegalStateException("You cannot call"
					+ " throwExceptionOnInputEnd(RuntimeException) because"
					+ " throwExceptionOnInputEnd(IOException) has already"
					+ " been called.");
			runtimeException = exception;
		}

		@Override
		public int read() throws IOException {
			int character = currentReader.read();
			if (character == -1)
				handleEmptyReader();
			return character;
		}

		private void handleEmptyReader() throws IOException {
			if (ioException != null)
				throw ioException;
			else if (runtimeException != null)
				throw runtimeException;
		}

		@Override
		public int read(byte[] buffer, int offset, int len) throws IOException {
			if (buffer == null)
				throw new NullPointerException();
			else if (offset < 0 || len < 0 || len > buffer.length - offset)
				throw new IndexOutOfBoundsException();
			else if (len == 0)
				return 0;
			else
				return readNextLine(buffer, offset, len);
		}

		private int readNextLine(byte[] buffer, int offset, int len)
				throws IOException {
			int c = read();
			if (c == -1)
				return -1;
			buffer[offset] = (byte) c;

			int i = 1;
			for (; (i < len) && !isCompleteLineWritten(buffer, i - 1); ++i) {
				byte read = (byte) read();
				if (read == -1)
					break;
				else
					buffer[offset + i] = read;
			}
			return i;
		}

		private boolean isCompleteLineWritten(byte[] buffer,
				int indexLastByteWritten) {
			byte[] separator = getProperty("line.separator")
				.getBytes(defaultCharset());
			int indexFirstByteOfSeparator = indexLastByteWritten
				- separator.length + 1;
			return indexFirstByteOfSeparator >= 0
				&& contains(buffer, separator, indexFirstByteOfSeparator);
		}

		private boolean contains(byte[] array, byte[] pattern, int indexStart) {
			for (int i = 0; i < pattern.length; ++i)
                if (array[indexStart + i] != pattern[i])
                    return false;
			return true;
		}
	}
}
