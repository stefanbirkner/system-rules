package org.junit.contrib.java.lang.system;

import static java.lang.System.err;
import static java.lang.System.setErr;

import java.io.PrintStream;

import org.junit.contrib.java.lang.system.internal.PrintStreamLog;

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
 *     public void captureErrorStream() {
 *       System.err.print("hello world");
 *       assertEquals("hello world", log.getLog());
 *     }
 *   }
 * </pre>
 */
public class StandardErrorStreamLog extends PrintStreamLog {
	@Override
	protected PrintStream getOriginalStream() {
		return err;
	}

	@Override
	protected void setStream(PrintStream wrappedLog) {
		setErr(wrappedLog);
	}
}
