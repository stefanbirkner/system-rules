package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.setProperty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.rules.ExternalResource;

/**
 * The {@code ProvideSystemProperty} rule provides an arbitrary value for a
 * system property to a test. After the test the original value is restored. You
 * can ensure that a property is not set by providing {@code null}.
 * <p>
 * Let's assume the system property {@code MyProperty} is not set and the system
 * property {@code OtherProperty} has the value {@code OtherValue}. Now run the
 * test
 * 
 * <pre>
 *   public void MyTest {
 *     &#064;Rule
 *     public final ProvideSystemProperty myPropertyHasMyValue
 *       = new ProvideSystemProperty("MyProperty", "MyValue");
 * 
 *     &#064;Rule
 *     public final ProvideSystemProperty otherPropertyIsMissing
 *       = new ProvideSystemProperty("OtherProperty", null);
 * 
 *     &#064;Test
 *     public void overrideProperty() {
 *       assertEquals("MyValue", System.getProperty("MyProperty"));
 *       assertNull(System.getProperty("OtherProperty"));
 *     }
 *   }
 * </pre>
 * 
 * The test succeeds and after the test, the system property {@code MyProperty}
 * is not set and the system property {@code OtherProperty} has the value
 * {@code OtherValue}.
 * <p>
 * You could also use a single instance of the rule to achieve the same effect:
 * 
 * <pre>
 *   public void MyTest {
 *     &#064;Rule
 *     public final ProvideSystemProperty properties
 *       = new ProvideSystemProperty("MyProperty", "MyValue")
 *                              .and("OtherProperty", null);
 * 
 *     &#064;Test
 *     public void overrideProperty() {
 *       assertEquals("MyValue", System.getProperty("MyProperty"));
 *       assertNull(System.getProperty("OtherProperty"));
 *     }
 *   }
 * </pre>
 */
public class ProvideSystemProperty extends ExternalResource {
	private final Map<String, String> properties = new LinkedHashMap<String, String>();
	private RestoreSystemProperties restoreSystemProperty;

	public ProvideSystemProperty(String name, String value) {
		addProperty(name, value);
	}

	public ProvideSystemProperty and(String name, String value) {
		addProperty(name, value);
		return this;
	}

	private void addProperty(String name, String value) {
		properties.put(name, value);
	}

	@Override
	protected void before() throws Throwable {
		restoreSystemProperty = new RestoreSystemProperties(collectPropertyNames());
		restoreSystemProperty.before();
		updateProperties();
	}

	private String[] collectPropertyNames() {
		return properties.keySet().toArray(new String[properties.size()]);
	}

	private void updateProperties() {
		for (Entry<String, String> property : properties.entrySet()) {
			String name = property.getKey();
			String value = property.getValue();
			updateProperty(name, value);
		}
	}

	private void updateProperty(String name, String value) {
		if (value == null)
			clearProperty(name);
		else
			setProperty(name, value);
	}

	@Override
	protected void after() {
		restoreSystemProperty.after();
	}
}
