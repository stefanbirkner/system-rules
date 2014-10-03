package org.junit.contrib.java.lang.system;

import org.junit.contrib.java.lang.system.internal.PrintStreamLog;

import java.io.PrintStream;

import static java.lang.System.err;
import static java.lang.System.setErr;

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
 */
public class StandardErrorStreamLog extends PrintStreamLog {
    /**
     * Constructs a new {@code StandardErrorStreamLog}.
     *
     * @param silent if {@code true} do not repeat output on stderr
     */
    public StandardErrorStreamLog(final boolean silent) {
        super(silent);
    }

    /** Constructs a new {@code StandardErrorStreamLog} which repeats output on stderr. */
    public StandardErrorStreamLog() {
        super(false);
    }

    @Override
	protected PrintStream getOriginalStream() {
		return err;
	}

	@Override
	protected void setStream(PrintStream wrappedLog) {
		setErr(wrappedLog);
	}
}
