package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
 *         = new RestoreSystemProperties();
 * 
 *     &#064;Test
 *     public void overrideProperty() {
 *       restoreSystemProperties.add("MyProperty");
 *       System.setProperty("MyProperty", "other value");
 *       ...
 *     }
 *   }
 * </pre>
 * 
 * After running the test, the system property {@code MyProperty} has the value
 * {@code MyValue} again. If you need do restore the same property for each test
 * then you can provide the property's name while creating the
 * {@code RestoreSystemProperties} rule.
 * 
 * <pre>
 * &#064;Rule
 * public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties(
 * 		&quot;MyProperty&quot;);
 * </pre>
 */
public class RestoreSystemProperties extends ExternalResource {
	private final List<String> properties = new ArrayList<String>();
	private final List<String> originalValues = new ArrayList<String>();

	/**
	 * Creates a {@code RestoreSystemProperties} rule that restores the
	 * specified properties.
	 * 
	 * @param properties
	 *            the properties' names.
	 */
	public RestoreSystemProperties(String... properties) {
		this.properties.addAll(asList(properties));
	}

	/**
	 * Add a property that is restored after the test. The
	 * {@code RestoreSystemProperties} restores the value of the property at the
	 * point of adding it.
	 * 
	 * @param property
	 *            the name of the property.
	 * @since 1.6.0
	 */
	public void add(String property) {
		properties.add(property);
		addValueForProperty(property);
	}

	@Override
	protected void before() throws Throwable {
		for (String property : properties)
			addValueForProperty(property);
	}

	@Override
	protected void after() {
		Iterator<String> itOriginalValues = originalValues.iterator();
		for (String property : properties)
			restore(property, itOriginalValues.next());
	}

	private void restore(String property, String originalValue) {
		if (originalValue == null)
			clearProperty(property);
		else
			setProperty(property, originalValue);
	}

	private void addValueForProperty(String property) {
		originalValues.add(getProperty(property));
	}
}
