package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;

import org.junit.rules.ExternalResource;

/**
 * The {@code ClearSystemProperty} rule clears a system property to a test.
 * After the test the original value is restored.
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
 */
public class ClearSystemProperty extends ExternalResource {

	private final RestoreSystemProperties restoreSystemProperty;
	private final String name;

	public ClearSystemProperty(String name) {
		this.name = name;
		this.restoreSystemProperty = new RestoreSystemProperties(name);
	}

	@Override
	protected void before() throws Throwable {
		backupOriginalValue();
		clearProperty(name);
	}

	@Override
	protected void after() {
		restoreOriginalValue();
	}

	private void backupOriginalValue() throws Throwable {
		restoreSystemProperty.before();
	}

	private void restoreOriginalValue() {
		restoreSystemProperty.after();
	}
}
