package org.junit.contrib.java.lang.system;

import static org.junit.contrib.java.lang.system.LogMode.LOG_AND_WRITE_TO_STREAM;
import static org.junit.contrib.java.lang.system.LogMode.LOG_ONLY;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @deprecated Please use {@link SystemErrRule}.
 *
 * <p>The {@code StandardErrorStreamLog} records writes to the standard error
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
 *
 * You can clear the log if you only want to test parts of the text written to
 * the standard error stream.
 *
 * <pre>
 *   &#064;Test
 *   public void captureErrorStream() {
 *     System.err.print("before");
 *     log.clear();
 *     System.err.print("afterwards");
 *     assertEquals("afterwards", log.getLog());
 *   }
 * </pre>
 *
 * <h2>Prevent output to the standard error stream</h2>
 * In general it is not necessary that a test writes to the standard error
 * stream. Avoiding this output may speed up the test and reduce the clutter
 * on the commandline.
 * <p>The test does not write to the stream if the rule is created with the
 * {@link org.junit.contrib.java.lang.system.LogMode#LOG_ONLY} mode.
 * <pre>
 * &#064;Rule
 * public final StandardErrorStreamLog log = new StandardErrorStreamLog(LOG_ONLY);</pre>
 */
@Deprecated
public class StandardErrorStreamLog implements TestRule {
	private final SystemErrRule systemErrRule = new SystemErrRule();
	/**
	 * @deprecated Please use
	 * {@link SystemErrRule#enableLog() new SystemErrRule().enableLog()}.
	 *
	 * <p>Creates a rule that records writes while they are still written to the
	 * standard error stream.
	 */
	public StandardErrorStreamLog() {
		this(LOG_AND_WRITE_TO_STREAM);
	}

	/**
	 * @deprecated Please use
	 * {@link SystemErrRule#enableLog() new SystemErrRule().enableLog()}
	 * instead of
	 * {@code new StandardErrorStreamLog(LogMode.LOG_AND_WRITE_TO_STREAM)} or
	 * {@link SystemErrRule#enableLog() new SystemErrRule().enableLog()}.{@link SystemErrRule#mute() mute()}
	 * instead of {@code new StandardErrorStreamLog(LogMode.LOG_ONLY)}.
	 *
	 * <p>Creates a rule that records writes to the standard error stream
	 * according to the specified {@code LogMode}.
	 *
	 * @param mode how the rule handles writes to the standard error stream.
	 * @throws java.lang.NullPointerException if {@code mode} is null.
	 */
	public StandardErrorStreamLog(LogMode mode) {
		if (mode == null)
			throw new NullPointerException("The LogMode is missing.");
		systemErrRule.enableLog();
		if (mode == LOG_ONLY)
			systemErrRule.mute();
	}

	/**
	 * @deprecated Please use
	 * {@link org.junit.contrib.java.lang.system.SystemErrRule#clearLog()}.
	 *
	 * <p>Clears the log. The log can be used again.
	 */
	@Deprecated
	public void clear() {
		systemErrRule.clearLog();
	}

	/**
	 * @deprecated Please use
	 * {@link org.junit.contrib.java.lang.system.SystemErrRule#getLog()}.
	 *
	 * <p>Returns the text written to the standard error stream.
	 *
	 * @return the text written to the standard error stream.
	 */
	@Deprecated
	public String getLog() {
		return systemErrRule.getLog();
	}

	public Statement apply(Statement base, Description description) {
		return systemErrRule.apply(base, description);
	}
}
