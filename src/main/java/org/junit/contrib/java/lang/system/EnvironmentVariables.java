package org.junit.contrib.java.lang.system;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Field;
import java.util.Map;

import static java.lang.System.getenv;

/**
 * The {@code EnvironmentVariables} rule allows you to set environment variables
 * from within your test. Any changes to environment variables are reverted
 * after the test.
 * <pre>
 * public class SystemOutTest {
 *   &#064;Rule
 *   public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
 *
 *   &#064;Test
 *   public void test() {
 *     environmentVariables.set("name", "value");
 *     assertEquals("value", System.getenv("name"));
 *   }
 * }
 * </pre>
 * <p><b>Warning:</b> This rule uses reflection for modifying some internals of
 * the class {@code System}. It does not work if your {@code SecurityManager}
 * disallows this.
 */
public class EnvironmentVariables implements TestRule {
	public void set(String name, String value) {
		Map<String, String> variables = getEditableMapOfVariables();
		if (value == null)
			variables.remove(name);
		else
			variables.put(name, value);
	}

	public Statement apply(final Statement base, Description description) {
		return new EnvironmentVariablesStatement(base);
	}

	private static class EnvironmentVariablesStatement extends Statement {
		final Statement baseStatement;
		Map<String, String> originalVariables;

		EnvironmentVariablesStatement(Statement baseStatement) {
			this.baseStatement = baseStatement;
		}

		@Override
		public void evaluate() throws Throwable {
			originalVariables = getenv();
			try {
				baseStatement.evaluate();
			} finally {
				restoreOriginalVariables();
			}
		}

		void restoreOriginalVariables() {
			Map<String, String> variables = getEditableMapOfVariables();
			variables.clear();
			variables.putAll(originalVariables);
		}
	}

	private static Map<String, String> getEditableMapOfVariables() {
		Class<?> classOfMap = getenv().getClass();
		try {
			Field field = classOfMap.getDeclaredField("m");
			field.setAccessible(true);
			return (Map<String, String>) field.get(getenv());
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
