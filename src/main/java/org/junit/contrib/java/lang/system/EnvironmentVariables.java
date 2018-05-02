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
 * for your test. All changes to environment variables are reverted after the
 * test.
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
 * <p>Common variables can be set directly after creating the rule
 * <pre>
 * public class EnvironmentVariablesTest {
 *   &#064;Rule
 *   public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
 *       .set("name", "value");
 *
 *   &#064;Test
 *   public void test() {
 *     assertEquals("value", System.getenv("name"));
 *   }
 * }
 * </pre>
 * <p>You can ensure that some environment variables are not set by calling
 * {@link #clear(String...)}.
 * <p><b>Warning:</b> This rule uses reflection for modifying internals of the
 * environment variables map. It fails if your {@code SecurityManager} forbids
 * such modifications.
 */
public class EnvironmentVariables implements TestRule {
	private final Map<String, String> buffer = new HashMap<String, String>();
	private boolean statementIsExecuting = false;

	/**
	 * Set the value of an environment variable.
	 *
	 * @param name the environment variable's name.
	 * @param value the environment variable's new value. May be {@code null}.
	 * @return the rule itself.
     */
	public EnvironmentVariables set(String name, String value) {
		if (statementIsExecuting)
			writeVariableToEnvMap(name, value);
		else
			writeVariableToBuffer(name, value);
		return this;
	}

	/**
	 * Delete multiple environment variables.
	 *
	 * @param names the environment variables' names.
	 * @return the rule itself.
     */
	public EnvironmentVariables clear(String... names) {
		for (String name: names)
			set(name, null);
		return this;
	}

	private void writeVariableToEnvMap(String name, String value) {
		set(getEditableMapOfVariables(), name, value);
		set(getTheCaseInsensitiveEnvironment(), name, value);
	}

	private void set(Map<String, String> variables, String name, String value) {
		if (variables != null) //theCaseInsensitiveEnvironment may be null
			if (value == null)
				variables.remove(name);
			else
				variables.put(name, value);
	}

	private void writeVariableToBuffer(String name, String value) {
		buffer.put(name, value);
	}

	private void copyVariablesFromBufferToEnvMap() {
		for (Map.Entry<String, String> nameAndValue: buffer.entrySet()) {
			writeVariableToEnvMap(
				nameAndValue.getKey(), nameAndValue.getValue());
		}
	}

	public Statement apply(Statement base, Description description) {
		return new EnvironmentVariablesStatement(base);
	}

	private class EnvironmentVariablesStatement extends Statement {
		final Statement baseStatement;
		Map<String, String> originalVariables;

		EnvironmentVariablesStatement(Statement baseStatement) {
			this.baseStatement = baseStatement;
		}

		@Override
		public void evaluate() throws Throwable {
			saveCurrentState();
			EnvironmentVariables.this.statementIsExecuting = true;
			try {
				copyVariablesFromBufferToEnvMap();
				baseStatement.evaluate();
			} finally {
				EnvironmentVariables.this.statementIsExecuting = false;
				restoreOriginalVariables();
			}
		}

		void saveCurrentState() {
			originalVariables = new HashMap<String, String>(getenv());
		}

		void restoreOriginalVariables() {
			restoreVariables(getEditableMapOfVariables());
			Map<String, String> theCaseInsensitiveEnvironment
				= getTheCaseInsensitiveEnvironment();
			if (theCaseInsensitiveEnvironment != null)
				restoreVariables(theCaseInsensitiveEnvironment);
		}

		void restoreVariables(Map<String, String> variables) {
			variables.clear();
			variables.putAll(originalVariables);
		}
	}

	private static Map<String, String> getEditableMapOfVariables() {
		Class<?> classOfMap = getenv().getClass();
		try {
			return getFieldValue(classOfMap, getenv(), "m");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("System Rules cannot access the field"
				+ " 'm' of the map System.getenv().", e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("System Rules expects System.getenv() to"
				+ " have a field 'm' but it has not.", e);
		}
	}

	/*
	 * The names of environment variables are case-insensitive in Windows.
	 * Therefore it stores the variables in a TreeMap named
	 * theCaseInsensitiveEnvironment.
     */
	private static Map<String, String> getTheCaseInsensitiveEnvironment() {
		try {
			Class<?> processEnvironment = forName("java.lang.ProcessEnvironment");
			return getFieldValue(
				processEnvironment, null, "theCaseInsensitiveEnvironment");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("System Rules expects the existence of"
				+ " the class java.lang.ProcessEnvironment but it does not"
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

	private static Map<String, String> getFieldValue(Class<?> klass,
			Object object, String name)
			throws NoSuchFieldException, IllegalAccessException {
		Field field = klass.getDeclaredField(name);
		field.setAccessible(true);
		return (Map<String, String>) field.get(object);
	}
}
