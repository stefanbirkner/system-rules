package org.junit.contrib.java.lang.system;

import static java.lang.System.out;
import static java.lang.System.setOut;

import java.io.PrintStream;

/**
 * The {@code StandardOutputStreamLog} captures writes to the standard output
 * stream. The text written is available via {@link #getLog()}.
 * 
 * <pre>
 *   public void MyTest {
 *     &#064;Rule
 *     public final StandardOutputStreamLog log = new StandardOutputStreamLog();
 * 
 *     &#064;Test
 *     public void overrideProperty() {
 *       System.out.print("hello world");
 *       assertEquals("hello world", log.getLog());
 *     }
 *   }
 * </pre>
 */
public class StandardOutputStreamLog extends PrintStreamLog {
	@Override
	PrintStream getOriginalStream() {
		return out;
	}

	@Override
	void setStream(PrintStream wrappedLog) {
		setOut(wrappedLog);
	}
}
