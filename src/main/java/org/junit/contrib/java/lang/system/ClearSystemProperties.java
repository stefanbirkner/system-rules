package org.junit.contrib.java.lang.system;

import org.junit.rules.ExternalResource;

/**
 * The {@code ClearSystemProperties} rule clears a set of system properties.
 * After the test the original values are restored.
 * 
 * Let's assume the system property {@code MyProperty} has the value
 * {@code MyValue}. Now run the test
 * 
 * <pre>
 *   public void MyTest {
 *     &#064;Rule
 *     public final ClearSystemProperties clearSystemProperties
 *         = new ClearSystemProperties();
 * 
 *     &#064;Test
 *     public void overrideProperty() {
 *       clearSystemProperties.clearProperty("MyProperty");
 *       assertNull(System.getProperty("MyProperty"));
 *       ...
 *     }
 *   }
 * </pre>
 * 
 * The test succeeds and after the test, the system property {@code MyProperty}
 * again has the value {@code MyValue}. If you need do restore the same
 * properties for each test then you can provide the properties' names while
 * creating the {@code ClearSystemProperties} rule.
 * 
 * <pre>
 * &#064;Rule
 * public final ClearSystemProperties clearSystemProperties = new ClearSystemProperties(
 * 		&quot;MyProperty&quot;, &quot;AnotherProperty&quot;);
 * </pre>
 */
public class ClearSystemProperties extends ExternalResource {
	private final RestoreSystemProperties restoreSystemProperty = new RestoreSystemProperties();
	private final String[] properties;

	/**
	 * Creates a {@code ClearSystemProperties} rule that clears the specified
	 * properties and restores them after the test.
	 * 
	 * @param properties
	 *            the properties' names.
	 */
	public ClearSystemProperties(String... properties) {
		this.properties = properties;
	}

	/**
	 * Clears the property and restores the value of the property at the point
	 * of clearing it.
	 * 
	 * @param property
	 *            the name of the property.
	 */
	public void clearProperty(String property) {
		restoreSystemProperty.add(property);
		System.clearProperty(property);
	}

	@Override
	protected void before() throws Throwable {
		clearProperties();
	}

	@Override
	protected void after() {
		restoreOriginalValue();
	}

	private void clearProperties() {
		for (String property : properties)
			clearProperty(property);
	}

	private void restoreOriginalValue() {
		restoreSystemProperty.after();
	}
}
