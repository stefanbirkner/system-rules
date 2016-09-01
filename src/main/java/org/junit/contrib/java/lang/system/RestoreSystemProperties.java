package org.junit.contrib.java.lang.system;

import static java.lang.System.getProperties;
import static java.lang.System.setProperties;

import java.util.Properties;

import org.junit.rules.ExternalResource;

/**
 * The {@code RestoreSystemProperties} rule undoes changes of system
 * properties when the test finishes (whether it passes or fails).
 * <p>Let's assume the system property {@code YourProperty} has the
 * value {@code YourValue}. Now run the test
 * <pre>
 *   public void YourTest {
 *     &#064;Rule
 *     public final TestRule restoreSystemProperties = new RestoreSystemProperties();
 *
 *     &#064;Test
 *     public void overrideProperty() {
 *       System.setProperty("YourProperty", "other value");
 *       assertEquals("other value", System.getProperty("YourProperty"));
 *     }
 *   }
 * </pre>
 * After running the test, the system property {@code YourProperty} has
 * the value {@code YourValue} again.
 */
public class RestoreSystemProperties extends ExternalResource {
	private Properties originalProperties;

	/**
	 * Creates a {@code RestoreSystemProperties} rule that restores all
	 * system properties.
	 *
	 * @deprecated Please use {@link #RestoreSystemProperties()}. The
	 * rule restores all properties. That's why you don't have to
	 * specify the properties anymore.
	 */
	@Deprecated
	public RestoreSystemProperties(String... properties) {
	}

	/**
	 * Creates a {@code RestoreSystemProperties} rule that restores all
	 * system properties.
	 *
	 * @since 1.8.0
	 */
	public RestoreSystemProperties() {
	}

	/**
	 * Does nothing.
	 *
	 * @since 1.6.0
	 * @deprecated Simply remove all calls to this method.
	 * {@code RestoreSystemProperties} restores all properties
	 * automatically. That's why you don't have to add the properties
	 * anymore.
	 */
	@Deprecated
	public void add(String property) {
	}

	@Override
	protected void before() throws Throwable {
		originalProperties = getProperties();
		setProperties(copyOf(originalProperties));
	}

	private Properties copyOf(Properties source) {
		Properties copy = new Properties();
		copy.putAll(source);
		return copy;
	}

	@Override
	protected void after() {
		setProperties(originalProperties);
	}
}
