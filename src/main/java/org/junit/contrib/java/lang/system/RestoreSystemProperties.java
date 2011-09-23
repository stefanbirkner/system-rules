package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;

import org.junit.rules.ExternalResource;

/**
 * The {@code RestoreSystemProperties} rule undoes changes of system properties.
 * 
 * Let's assume the system property {@code MyProperty} has the value
 * {@code MyValue}. Now run the test
 * 
 * <pre>
 *   public void MyTest {
 *     &#064;Rule
 *     public final RestoreSystemProperties restoreSystemProperties
 *       = new RestoreSystemProperties("MyProperty");
 * 
 *     &#064;Test
 *     public void overrideProperty() {
 *       System.setProperty("MyProperty", "other value");
 *       ...
 *     }
 *   }
 * </pre>
 * 
 * After running the test, the system property {@code MyProperty} has still the
 * value {@code MyValue}.
 */
public class RestoreSystemProperties extends ExternalResource {
	private final String[] properties;
	private String[] originalValues;

	public RestoreSystemProperties(String... properties) {
		this.properties = properties;
	}

	@Override
	protected void before() throws Throwable {
		originalValues = new String[properties.length];
		for (int i = 0; i < properties.length; i++)
			originalValues[i] = getProperty(properties[i]);
	}

	@Override
	protected void after() {
		for (int i = 0; i < properties.length; i++)
			if (originalValues[i] == null)
				clearProperty(properties[i]);
			else
				setProperty(properties[i], originalValues[i]);
	}
}
