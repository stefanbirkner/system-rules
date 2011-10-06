package org.junit.contrib.java.lang.system;

import static java.lang.System.in;
import static java.lang.System.setIn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.rules.ExternalResource;

/**
 * The {@code TextFromStandardInputStream} rule replaces {@code System.in} with
 * another {@code InputStream}, which provides an arbitrary text. The original
 * {@code System.in} is restored after the test.
 * 
 * <pre>
 *   public void MyTest {
 *     &#064;Rule
 *     public final TextFromStandardInputStream textFromStandardInputStream
 *       = new TextFromStandardInputStream("foo");
 * 
 *     &#064;Test
 *     public void readTextFromStandardInputStream() {
 *       BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
 *       assertEquals("foo", reader.readLine());
 *     }
 *   }
 * </pre>
 */
public class TextFromStandardInputStream extends ExternalResource {
	private final String text;
	private InputStream originalIn;

	public TextFromStandardInputStream(String text) {
		this.text = text;
	}

	@Override
	protected void before() throws Throwable {
		originalIn = in;
		InputStream is = new ByteArrayInputStream(text.getBytes());
		setIn(is);
	}

	@Override
	protected void after() {
		setIn(originalIn);
	}
}
