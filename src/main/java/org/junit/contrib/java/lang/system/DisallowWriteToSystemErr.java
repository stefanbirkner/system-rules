package org.junit.contrib.java.lang.system;

import org.junit.contrib.java.lang.system.internal.DisallowWrite;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.junit.contrib.java.lang.system.internal.PrintStreamHandler.SYSTEM_ERR;

/**
 * {@code DisallowWriteToSystemErr} lets a test fail if it tries to write
 * something to {@code System.err}.
 *
 * <p>For that purpose you only have to add {@code DisallowWriteToSystemErr}
 * rule to your test class
 * <pre>
 * public class TestClass {
 *   &#064;Rule
 *   public final DisallowWriteToSystemErr disallowWriteToSystemErr
 *     = new DisallowWriteToSystemErr();
 *
 *   &#064;Test
 *   public void this_test_fails() {
 *     System.err.println("some text");
 *   }
 * }
 * </pre>
 *
 * @see DisallowWriteToSystemOut
 * @since 1.14.0
 */
public class DisallowWriteToSystemErr implements TestRule {
	private final DisallowWrite disallowWrite = new DisallowWrite(SYSTEM_ERR);

	public Statement apply(final Statement base, Description description) {
		return disallowWrite.createStatement(base);
	}
}
