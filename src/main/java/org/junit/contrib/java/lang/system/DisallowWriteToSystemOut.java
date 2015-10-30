package org.junit.contrib.java.lang.system;

import org.junit.contrib.java.lang.system.internal.DisallowWrite;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.junit.contrib.java.lang.system.internal.PrintStreamHandler.SYSTEM_OUT;

/**
 * {@code DisallowWriteToSystemOut} lets a test fail if it tries to write
 * something to {@code System.out}.
 *
 * <p>For that purpose you only have to add {@code DisallowWriteToSystemOut}
 * rule to your test class
 * <pre>
 * public class TestClass {
 *   &#064;Rule
 *   public final DisallowWriteToSystemOut disallowWriteToSystemOut
 *     = new DisallowWriteToSystemOut();
 *
 *   &#064;Test
 *   public void this_test_fails() {
 *     System.out.println("some text");
 *   }
 * }
 * </pre>
 *
 * @see DisallowWriteToSystemErr
 * @since 1.14.0
 */
public class DisallowWriteToSystemOut implements TestRule {
	private final DisallowWrite disallowWrite = new DisallowWrite(SYSTEM_OUT);

	public Statement apply(final Statement base, Description description) {
		return disallowWrite.createStatement(base);
	}
}
