package org.junit.contrib.java.lang.system;

import static org.junit.contrib.java.lang.system.internal.PrintStreamHandler.SYSTEM_ERR;

import org.junit.contrib.java.lang.system.internal.LogPrintStream;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * The {@code SystemErrRule} intercepts the writes to
 * {@code System.err}. It is used to make assertions about the text
 * that is written to {@code System.err} or to mute {@code System.err}.
 *
 * <h2>Assertions</h2>
 *
 * <p>{@code SystemErrRule} may be used for verifying the text that is
 * written to {@code System.err}.
 *
 * <pre>
 * public class SystemErrTest {
 *   &#064;Rule
 *   public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();
 *
 *   &#064;Test
 *   public void test() {
 *     System.err.print("some text");
 *     assertEquals("some text", systemErrRule.getLog());
 *   }
 * }
 * </pre>
 *
 * <p>If your code under test writes the correct new line characters to
 * {@code System.err} then the test output is different at different systems.
 * {@link #getLogWithNormalizedLineSeparator()} provides a log that always uses
 * {@code \n} as line separator. This makes it easy to write appropriate
 * assertions that work on all systems.
 *
 * <pre>
 * public class SystemErrTest {
 *   &#064;Rule
 *   public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();
 *
 *   &#064;Test
 *   public void test() {
 *     System.err.print(String.format("some text%n"));
 *     assertEquals("some text\n", systemErrRule.getLogWithNormalizedLineSeparator());
 *   }
 * }
 * </pre>
 *
 * <p>You don't have to enable logging for every test. It can be enabled for
 * specific tests only.
 *
 * <pre>
 * public class SystemErrTest {
 *   &#064;Rule
 *   public final SystemErrRule systemErrRule = new SystemErrRule();
 *
 *   &#064;Test
 *   public void testWithLogging() {
 *     systemErrRule.enableLog()
 *     System.err.print("some text");
 *     assertEquals("some text", systemErrRule.getLog());
 *   }
 *
 *   &#064;Test
 *   public void testWithoutLogging() {
 *     System.err.print("some text");
 *   }
 * }
 * </pre>
 *
 * <p>If you want to verify parts of the output only then you can clear the log
 * during a test.
 *
 * <pre>
 * public class SystemErrTest {
 *   &#064;Rule
 *   public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();
 *
 *   &#064;Test
 *   public void test() {
 *     System.err.print("uninteresting things");
 *     systemErrRule.clearLog()
 *     System.err.print("interesting things");
 *     assertEquals("interesting things", systemErrRule.getLog());
 *   }
 * }
 * </pre>
 *
 * <h2>Muting</h2>
 *
 * <p>Usually the output of a test to {@code System.err} does not have to be
 * visible. It may even slowdown the test. {@code SystemErrRule} can
 * be used to suppress this output.
 *
 * <pre>
 * public class SystemErrTest {
 *   &#064;Rule
 *   public final SystemErrRule systemErrRule = new SystemErrRule().mute();
 *
 *   &#064;Test
 *   public void test() {
 *     System.err.print("some text"); //is not visible on the console
 *   }
 * }
 * </pre>
 *
 * <p>You don't have to mute {@code System.err} for every test. It can be muted for
 * specific tests only.
 *
 * <pre>
 * public class SystemErrTest {
 *   &#064;Rule
 *   public final SystemErrRule systemErrRule = new SystemErrRule();
 *
 *   &#064;Test
 *   public void testWithSuppressedOutput() {
 *     systemErrRule.mute()
 *     System.err.print("some text");
 *   }
 *
 *   &#064;Test
 *   public void testWithNormalOutput() {
 *     System.err.print("some text");
 *   }
 * }
 * </pre>
 *
 * <p>In case of a failed test it is sometimes helpful to see the output. This
 * is when the method {@link #muteForSuccessfulTests()} comes into play.
 *
 * <pre>
 * public class SystemErrTest {
 *   &#064;Rule
 *   public final SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests();
 *
 *   &#064;Test
 *   public void testWithSuppressedOutput() {
 *     System.err.print("some text");
 *   }
 *
 *   &#064;Test
 *   public void testWithNormalOutput() {
 *     System.err.print("some text");
 *     fail();
 *   }
 * }
 * </pre>
 *
 * <h2>Combine Logging and Muting</h2>
 *
 * <p>Logging and muting can be combined. No output is actually written to
 * {@code System.err} but everything that would have been written is available
 * from the log.
 *
 * <pre>
 * public class SystemErrTest {
 *   &#064;Rule
 *   public final SystemErrRule systemErrRule = new SystemErrRule().mute().enableLog();
 *
 *   &#064;Test
 *   public void test() {
 *     System.err.print("some text"); //is not visible on the console
 *     assertEquals("some text", systemErrRule.getLog()); //succeeds
 *   }
 * }
 * </pre>
 */
public class SystemErrRule implements TestRule {
	private LogPrintStream logPrintStream = new LogPrintStream(SYSTEM_ERR);

	/**
	 * Suppress the output to {@code System.err}.
	 *
	 * @return the rule itself.
	 */
	public SystemErrRule mute() {
		logPrintStream.mute();
		return this;
	}

	/**
	 * Suppress the output to {@code System.err} for successful tests only.
	 * The output is still written to {@code System.err} for failing tests.
	 *
	 * @return the rule itself.
	 */
	public SystemErrRule muteForSuccessfulTests() {
		logPrintStream.muteForSuccessfulTests();
		return this;
	}

	/**
	 * Clears the current log.
	 */
	public void clearLog() {
		logPrintStream.clearLog();
	}

	/**
	 * Returns the text that is written to {@code System.err} since
	 * {@link #enableLog()} (respectively {@link #clearLog()} has been called.
	 *
	 * @return the text that is written to {@code System.err} since
	 * {@link #enableLog} (respectively {@link #clearLog()} has been called.
	 */
	public String getLog() {
		return logPrintStream.getLog();
	}

	/**
	 * Returns the text that is written to {@code System.err} since
	 * {@link #enableLog()} (respectively {@link #clearLog()} has been called.
	 * New line characters are replaced with a single {@code \n}.
	 *
	 * @return the normalized log.
	 */
	public String getLogWithNormalizedLineSeparator() {
		return logPrintStream.getLogWithNormalizedLineSeparator();
	}

	/**
	 * Start logging of everything that is written to {@code System.err}.
	 *
	 * @return the rule itself.
	 */
	public SystemErrRule enableLog() {
		logPrintStream.enableLog();
		return this;
	}

	public Statement apply(Statement base, Description description) {
		return logPrintStream.createStatement(base);
	}
}
