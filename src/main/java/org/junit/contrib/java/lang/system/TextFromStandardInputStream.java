package org.junit.contrib.java.lang.system;

import static java.lang.System.getProperty;
import static java.lang.System.in;
import static java.lang.System.setIn;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

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
 *       systemInMock.provideText("foo");
 *       Scanner scanner = new Scanner(System.in);
 *       assertEquals("foo", scanner.nextLine());
 *     }
 *   }
 * </pre>
 *
 * <h3>Multiple Texts</h3>
 * You can simulate a user that stops typing and continues afterwards
 * by providing multiple texts.
 * <pre>
 *   &#064;Test
 *   public void readTextFromStandardInputStream() {
 *     systemInMock.provideText("foo\n", "bar\n");
 *     Scanner firstScanner = new Scanner(System.in);
 *     scanner.nextLine();
 *     Scanner secondScanner = new Scanner(System.in);
 *     assertEquals("bar", scanner.nextLine());
 *   }
 * </pre>
 *
 * <p>If every text is a single line then you can use the method
 * {@link #provideLines(String...)} that appends the end of line
 * characters according to {@code System.getProperty("line.separator")}
 * to each text.
 * <pre>
 *   &#064;Test
 *   public void readTextFromStandardInputStream() {
 *     systemInMock.provideLines("foo", "bar");
 *     Scanner firstScanner = new Scanner(System.in);
 *     scanner.nextLine();
 *     Scanner secondScanner = new Scanner(System.in);
 *     assertEquals("bar", scanner.nextLine());
 *   }
 * </pre>
 *
 * <h3>Throwing Exceptions</h3>
 * <p>{@code TextFromStandardInputStream} can also simulate a {@code System.in}
 * that throws an {@code IOException} or {@code RuntimeException}. Use
 * <pre>   systemInMock.{@link #throwExceptionOnInputEnd(IOException)}</pre>
 * <p>or
 * <pre>   systemInMock.{@link #throwExceptionOnInputEnd(RuntimeException)}</pre>
 * <p>If you call {@link #provideLines(String...)} or
 * {@link #provideText(String...)} in addition then the exception is thrown
 * after the text has been read from {@code System.in}.
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
	 * @deprecated use {@link #provideText(String...)}
	 */
	@Deprecated
	public TextFromStandardInputStream(String text) {
		provideText(text);
	}

	/**
	 * Set the text that is returned by {@code System.in}. You can
	 * provide multiple texts. In that case {@code System.in.read()}
	 * returns -1 once when the end of a single text is reached and
	 * continues with the next text afterwards.
	 *
	 * @param texts a list of texts.
	 */
	public void provideText(String... texts) {
		systemInMock.provideText(asList(texts));
	}

	/**
	 * Set the lines that are returned by {@code System.in}.
	 * {@code System.getProperty("line.separator")} is used for the end
	 * of line. {@code System.in.read()} returns -1 once when the end
	 * of a single line is reached and continues with the next line
	 * afterwards.
	 *
	 * @param lines a list of lines.
	 */
	public void provideLines(String... lines) {
		String[] texts = appendEndOfLineToLines(lines);
		provideText(texts);
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

	private String[] appendEndOfLineToLines(String[] lines) {
		String[] texts = new String[lines.length];
		for (int index = 0; index < lines.length; ++index)
			texts[index] = lines[index] + getProperty("line.separator");
		return texts;
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
		private Iterator<String> texts;
		private StringReader currentReader;
		private IOException ioException;
		private RuntimeException runtimeException;

		void provideText(List<String> texts) {
			this.texts = texts.iterator();
			updateReader();
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
			if (texts.hasNext())
				updateReader();
			else if (ioException != null)
				throw ioException;
			else if (runtimeException != null)
				throw runtimeException;
		}

		private void updateReader() {
			if (texts.hasNext())
				currentReader = new StringReader(texts.next());
		}
	}
}
