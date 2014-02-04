package org.junit.contrib.java.lang.system;

import static java.lang.System.out;
import static java.lang.System.setOut;

import java.io.PrintStream;

import org.junit.contrib.java.lang.system.internal.PrintStreamLog;

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
 *     public void captureOutputStream() {
 *       System.out.print("hello world");
 *       assertEquals("hello world", log.getLog());
 *     }
 *   }
 * </pre>
 *
 * You can clear the log if you only want to test parts of the text written to
 * the standard output stream.
 *
 * <pre>
 *   &#064;Test
 *   public void captureOutputStream() {
 *     System.out.print("before");
 *     log.clear();
 *     System.out.print("afterwards");
 *     assertEquals("afterwards", log.getLog());
 *   }
 * </pre>
 */
public class StandardOutputStreamLog extends PrintStreamLog {
	@Override
	protected PrintStream getOriginalStream() {
		return out;
	}

	@Override
	protected void setStream(PrintStream wrappedLog) {
		setOut(wrappedLog);
	}
}
