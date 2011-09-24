package org.junit.contrib.java.lang.system;

import static java.lang.System.err;
import static java.lang.System.setErr;

import java.io.PrintStream;

/**
 * The {@code StandardErrorStreamLog} captures writes to the standard error
 * stream. The text written is available via {@link #getLog()}.
 * 
 * <pre>
 *   public void MyTest {
 *     &#064;Rule
 *     public final StandardErrorStreamLog log = new StandardErrorStreamLog();
 * 
 *     &#064;Test
 *     public void overrideProperty() {
 *       System.err.print("hello world");
 *       assertEquals("hello world", log.getLog());
 *     }
 *   }
 * </pre>
 */
public class StandardErrorStreamLog extends StandardStreamLog {
	@Override
	PrintStream getOriginalStream() {
		return err;
	}

	@Override
	void setStream(PrintStream wrappedLog) {
		setErr(wrappedLog);
	}
}
