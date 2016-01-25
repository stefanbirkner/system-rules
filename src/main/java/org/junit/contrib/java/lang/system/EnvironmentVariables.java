package org.junit.contrib.java.lang.system;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Class.forName;
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
		updateVariable(getEditableMapOfVariables(), name, value);
		updateVariable(getEditableMapOfCaseInsensitiveVariables(), name, value);
	}

	private void updateVariable(Map<String, String> variables, String name,
			String value) {
		if (variables != null) //theCaseInsensitiveEnvironment may be null
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
		Map<String, String> originalCaseInsensitiveEnvironment;

		EnvironmentVariablesStatement(Statement baseStatement) {
			this.baseStatement = baseStatement;
		}

		@Override
		public void evaluate() throws Throwable {
			saveCurrentState();
			try {
				baseStatement.evaluate();
			} finally {
				restoreOriginalVariables();
			}
		}

		void saveCurrentState() {
			originalVariables = new HashMap<String, String>(getenv());
			Map<String, String> theCaseInsensitiveEnvironment
				= getEditableMapOfCaseInsensitiveVariables();
			if (theCaseInsensitiveEnvironment != null)
				originalCaseInsensitiveEnvironment
					= new HashMap<String, String>(theCaseInsensitiveEnvironment);
		}

		void restoreOriginalVariables() {
			restoreVariables(getEditableMapOfVariables(), originalVariables);
			Map<String, String> theCaseInsensitiveEnvironment
				= getEditableMapOfCaseInsensitiveVariables();
			if (theCaseInsensitiveEnvironment != null)
				restoreVariables(theCaseInsensitiveEnvironment,
					originalCaseInsensitiveEnvironment);
		}

		void restoreVariables(Map<String, String> current,
				Map<String, String> original) {
			current.clear();
			current.putAll(original);
		}
	}

	private static Map<String, String> getEditableMapOfVariables() {
		Class<?> classOfMap = getenv().getClass();
		try {
			return getMapOfVariables(classOfMap, getenv(), "m");
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<String, String> getEditableMapOfCaseInsensitiveVariables() {
		try {
			Class<?> processEnvironment = forName("java.lang.ProcessEnvironment");
			return getMapOfVariables(
				processEnvironment, null, "theCaseInsensitiveEnvironment");
		} catch (NoSuchFieldException e) {
			//this field is only available for Windows
			return null;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<String, String> getMapOfVariables(Class<?> klass,
			Object object, String name) throws NoSuchFieldException {
		Field field = klass.getDeclaredField(name);
		field.setAccessible(true);
		try {
			return (Map<String, String>) field.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
