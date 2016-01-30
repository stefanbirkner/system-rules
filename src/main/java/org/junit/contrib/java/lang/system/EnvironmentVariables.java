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
 * within your test. Any changes to environment variables are reverted after
 * the test.
 * <pre>
 * public class EnvironmentVariablesTest {
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
 * the environment variables map. It does not work if your
 * {@code SecurityManager} disallows this.
 */
public class EnvironmentVariables implements TestRule {
	/**
	 * Set the value of an environment variable. You can delete an environment
	 * variable by setting it to {@code null}.
	 *
	 * @param name the environment variable's name.
	 * @param value the environment variable's new value.
     */
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
		}

		void restoreOriginalVariables() {
			restoreVariables(getEditableMapOfVariables());
			Map<String, String> theCaseInsensitiveEnvironment
				= getEditableMapOfCaseInsensitiveVariables();
			if (theCaseInsensitiveEnvironment != null)
				restoreVariables(theCaseInsensitiveEnvironment);
		}

		void restoreVariables(Map<String, String> current) {
			current.clear();
			current.putAll(originalVariables);
		}
	}

	private static Map<String, String> getEditableMapOfVariables() {
		Class<?> classOfMap = getenv().getClass();
		try {
			return getMapOfVariables(classOfMap, getenv(), "m");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("System Rules cannot access the field"
				+ " 'm' of the map System.getenv().", e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("System Rules expects System.getenv() to"
				+ " have a field 'm' but is hasn't.", e);
		}
	}

	private static Map<String, String> getEditableMapOfCaseInsensitiveVariables() {
		try {
			Class<?> processEnvironment = forName("java.lang.ProcessEnvironment");
			return getMapOfVariables(
				processEnvironment, null, "theCaseInsensitiveEnvironment");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("System Rules expects the existence of"
				+ " the class  java.lang.ProcessEnvironment but it does not"
				+ " exist.", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("System Rules cannot access the static"
				+ " field 'theCaseInsensitiveEnvironment' of the class"
				+ " java.lang.ProcessEnvironment.", e);
		} catch (NoSuchFieldException e) {
			//this field is only available for Windows
			return null;
		}
	}

	private static Map<String, String> getMapOfVariables(Class<?> klass,
			Object object, String name)
			throws NoSuchFieldException, IllegalAccessException {
		Field field = klass.getDeclaredField(name);
		field.setAccessible(true);
		return (Map<String, String>) field.get(object);
	}
}
