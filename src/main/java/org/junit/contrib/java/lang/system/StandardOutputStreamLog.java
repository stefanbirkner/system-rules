package org.junit.contrib.java.lang.system;

import static org.junit.contrib.java.lang.system.LogMode.LOG_AND_WRITE_TO_STREAM;
import static org.junit.contrib.java.lang.system.LogMode.LOG_ONLY;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @deprecated Please use {@link SystemOutRule}.
 *
 * <p>The {@code StandardOutputStreamLog} records writes to the standard output
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
@Deprecated
public class StandardOutputStreamLog implements TestRule {
	private final SystemOutRule systemOut = new SystemOutRule();
	/**
	 * @deprecated Please use
	 * {@link SystemOutRule#enableLog() new SystemOutRule().enableLog()}.
	 *
	 * <p>Creates a rule that records writes while they are still written to
	 * the standard output stream.
	 */
	public StandardOutputStreamLog() {
		this(LOG_AND_WRITE_TO_STREAM);
	}

	/**
	 * @deprecated Please use
	 * {@link SystemOutRule#enableLog() new SystemOutRule().enableLog()}
	 * instead of
	 * {@code new StandardOutputStreamLog(LogMode.LOG_AND_WRITE_TO_STREAM)} or
	 * {@link SystemOutRule#enableLog() new SystemOutRule().enableLog()}.{@link SystemOutRule#mute() mute()}
	 * instead of {@code new StandardOutputStreamLog(LogMode.LOG_ONLY)}.
	 *
	 * <p>Creates a rule that records writes to the standard output stream
	 * according to the specified {@code LogMode}.
	 *
	 * @param mode how the rule handles writes to the standard output stream.
	 * @throws java.lang.NullPointerException if {@code mode} is null.
	 */
	public StandardOutputStreamLog(LogMode mode) {
		if (mode == null)
			throw new NullPointerException("The LogMode is missing.");
		systemOut.enableLog();
		if (mode == LOG_ONLY)
			systemOut.mute();
	}

	/**
	 * @deprecated Please use
	 * {@link org.junit.contrib.java.lang.system.SystemOutRule#clearLog()}.
	 *
	 * <p>Clears the log. The log can be used again.
	 */
	@Deprecated
	public void clear() {
		systemOut.clearLog();
	}

	/**
	 * @deprecated Please use
	 * {@link org.junit.contrib.java.lang.system.SystemOutRule#getLog()}.
	 *
	 * <p>Returns the text written to the standard output stream.
	 *
	 * @return the text written to the standard output stream.
	 */
	@Deprecated
	public String getLog() {
		return systemOut.getLog();
	}

	public Statement apply(Statement base, Description description) {
		return systemOut.apply(base, description);
	}
}
