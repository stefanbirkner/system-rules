package org.junit.contrib.java.lang.system;

import org.junit.rules.ExternalResource;

/**
 * The {@code ClearSystemProperties} rule clears a set of system
 * properties when the test starts and restores their original values
 * when the test finishes (whether it passes or fails).
 * <p>Supposing that the system property {@code YourProperty} has the
 * value {@code YourValue}. Now run the test
 * <pre>
 * public void YourTest {
 *   &#064;Rule
 *   public final TestRule clearSystemProperties
 *     = new ClearSystemProperties("YourProperty");
 *
 *   &#064;Test
 *   public void verifyProperty() {
 *     assertNull(System.getProperty("YourProperty"));
 *   }
 * }
 * </pre>
 * The test succeeds and afterwards the system property
 * {@code YourProperty} has the value {@code YourValue} again.
 * <p>The {@code ClearSystemProperties} rule accepts a list of
 * properties in case you need to clear multiple properties:
 * <pre>
 * &#064;Rule
 * public final TestRule clearSystemProperties
 *   = new ClearSystemProperties("first", "second", "third");
 * </pre>
 */
public class ClearSystemProperties extends ExternalResource {
	private final RestoreSystemProperties restoreSystemProperty = new RestoreSystemProperties();
	private final String[] properties;

	/**
	 * Creates a {@code ClearSystemProperties} rule that clears the specified
	 * properties and restores their original values when the test finishes
	 * (whether it passes or fails).
	 *
	 * @param properties the properties' names.
	 */
	public ClearSystemProperties(String... properties) {
		this.properties = properties;
	}

	/**
	 * Clears the property and restores the value of the property at the point
	 * of clearing it.
	 *
	 * @param property the name of the property.
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
