package org.junit.contrib.java.lang.system;

import static java.lang.System.in;
import static java.lang.System.setIn;

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
 *       systemInMock.provide("foo");
 *       Scanner scanner = new Scanner(System.in);
 *       assertEquals("foo", scanner.nextLine());
 *     }
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
	 * @param text
	 *            this text is return by {@code System.in}.
	 * @deprecated use {@link #provideText(String)}
	 */
	@Deprecated
	public TextFromStandardInputStream(String text) {
		provideText(text);
	}

	public void provideText(String text) {
		systemInMock.provideText(text);
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
		private StringReader reader;

		public void provideText(String text) {
			reader = new StringReader(text);
		}

		@Override
		public int read() throws IOException {
			return reader.read();
		}
	}
}
