package org.junit.contrib.java.lang.system;

import static java.lang.System.out;
import static java.lang.System.setOut;
import static org.junit.contrib.java.lang.system.LogMode.LOG_AND_WRITE_TO_STREAM;

import java.io.PrintStream;

import org.junit.contrib.java.lang.system.internal.PrintStreamLog;

/**
 * The {@code StandardOutputStreamLog} records writes to the standard output
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
 * <h2>Prevent output to the standard output stream</h2>
 * In general it is not necessary that a test writes to the standard output
 * stream. Avoiding this output may speed up the test and reduce the clutter
 * on the commandline.
 * <p>The test does not write to the stream if the rule is created with the
 * {@link org.junit.contrib.java.lang.system.LogMode#LOG_ONLY} mode.
 * <pre>
 * &#064;Rule
 * public final StandardOutputStreamLog log = new StandardOutputStreamLog(LOG_ONLY);</pre>
 */
public class StandardOutputStreamLog extends PrintStreamLog {
	/**
	 * Creates a rule that records writes while they are still written to the
	 * standard output stream.
	 */
	public StandardOutputStreamLog() {
		this(LOG_AND_WRITE_TO_STREAM);
	}

	/**
	 * Creates a rule that records writes to the standard output stream
	 * according to the specified {@code LogMode}.
	 * @param mode how the rule handles writes to the standard output stream.
	 * @throws java.lang.NullPointerException if {@code mode} is null.
	 */
	public StandardOutputStreamLog(LogMode mode) {
		super(mode);
	}

	@Override
	protected PrintStream getOriginalStream() {
		return out;
	}

	@Override
	protected void setStream(PrintStream wrappedLog) {
		setOut(wrappedLog);
	}
}
