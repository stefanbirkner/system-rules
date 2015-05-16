package org.junit.contrib.java.lang.system;

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
 *       systemInMock.provide("foo");
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
 *     systemInMock.provide("foo\n", "bar\n");
 *     Scanner firstScanner = new Scanner(System.in);
 *     scanner.nextLine();
 *     Scanner secondScanner = new Scanner(System.in);
 *     assertEquals("bar", scanner.nextLine());
 *   }
 * </pre>
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
	 * @param texts a list of texts.
	 */
	public void provideText(String... texts) {
		systemInMock.provideText(asList(texts));
	}
	
	/**
	 * Set the lines that are returned by {@code System.in},
	 * each in a new line separated by System.lineSeperator().
	 * You can provide multiple lines. In that case {@code System.in.read()}
	 * returns -1 once when the end of a single text is reached and
	 * continues with the next text afterwards.
	 * @param lines a list of lines.
	 */
	public void provideLines(String... lines) {
		String[] texts = new String[lines.length];
		
		for (int index = 0; index < lines.length; index++) {
			texts[index] = lines[index] + System.lineSeparator();
		}
		
		provideText(texts);
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

		public void provideText(List<String> texts) {
			this.texts = texts.iterator();
			optionallyCreateReaderForNextText();
		}

		@Override
		public int read() throws IOException {
			if (currentReader == null)
				return -1;
			else
				return readFromExistingReader();
		}

		private int readFromExistingReader() throws IOException {
			int character = currentReader.read();
			if (character == -1)
				optionallyCreateReaderForNextText();
			return character;
		}

		private void optionallyCreateReaderForNextText() {
			if (texts.hasNext())
				currentReader = new StringReader(texts.next());
			else
				currentReader = null;
		}
	}

	
}
