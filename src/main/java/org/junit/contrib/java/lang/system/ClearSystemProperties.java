package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;

import org.junit.rules.ExternalResource;

/**
 * The {@code ClearSystemProperty} rule clears a set of system properties to a
 * test. After the test the original values are restored.
 * 
 * Let's assume the system property {@code MyProperty} has the value
 * {@code MyValue}. Now run the test
 * 
 * <pre>
 *   public void MyTest {
 *     &#064;Rule
 *     public final ClearSystemProperty myPropertyIsCleared
 *       = new ClearSystemProperty("MyProperty");
 * 
 *     &#064;Test
 *     public void overrideProperty() {
 *       assertNull(System.getProperty("MyProperty"));
 *     }
 *   }
 * </pre>
 * 
 * The test succeeds and after the test, the system property {@code MyProperty}
 * has the value {@code MyValue}.
 * 
 * The {@code ClearSystemProperty} rule accepts a list of properties:
 * 
 * <pre>
 * &#064;Rule
 * public final ClearSystemProperty myPropertyIsCleared = new ClearSystemProperty(
 * 		&quot;first&quot;, &quot;second&quot;, &quot;third&quot;);
 * </pre>
 */
public class ClearSystemProperties extends ExternalResource {

	private final RestoreSystemProperties restoreSystemProperty;
	private final String[] names;

	public ClearSystemProperties(String... names) {
		this.names = names;
		this.restoreSystemProperty = new RestoreSystemProperties(names);
	}

	@Override
	protected void before() throws Throwable {
		backupOriginalValue();
		clearProperties();
	}

	@Override
	protected void after() {
		restoreOriginalValue();
	}

	private void backupOriginalValue() throws Throwable {
		restoreSystemProperty.before();
	}

	private void clearProperties() {
		for (String name : names)
			clearProperty(name);
	}

	private void restoreOriginalValue() {
		restoreSystemProperty.after();
	}
}
