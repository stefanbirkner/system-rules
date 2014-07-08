package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.contrib.java.lang.system.internal.RestoreSpecificSystemProperties;
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
 *     public final ProvideSystemProperty provideSystemProperty
 *         = new ProvideSystemProperty();
 *
 *     &#064;Test
 *     public void overridesProperty() {
 *       provideSystemProperty.setProperty("MyProperty", "MyValue");
 *       assertEquals("MyValue", System.getProperty("MyProperty"));
 *     }
 *
 *     &#064;Test
 *     public void deletesProperty() {
 *       provideSystemProperty.setProperty("OtherProperty", null);
 *       assertNull(System.getProperty("OtherProperty"));
 *     }
 *   }
 * </pre>
 *
 * Both tests succeed and after the tests, the system property
 * {@code MyProperty} is not set and the system property {@code OtherProperty}
 * has the value {@code OtherValue}. If you need do provide the same properties
 * for each test then you can specify the values while creating the
 * {@code ProvideSystemProperty} rule.
 *
 * <pre>
 * &#064;Rule
 * public final ProvideSystemProperty properties = new ProvideSystemProperty(
 * 		&quot;MyProperty&quot;, &quot;MyValue&quot;).and(&quot;OtherProperty&quot;, null);
 * </pre>
 *
 * You can use a properties file to supply properties for the
 * ProvideSystemProperty rule. The file can be from the file system or the class
 * path. In the first case use
 *
 * <pre>
 * &#064;Rule
 * public final ProvideSystemProperty properties = ProvideSystemProperty
 * 		.fromFile(&quot;/home/myself/example.properties&quot;);
 * </pre>
 *
 * and in the second case use
 *
 * <pre>
 * &#064;Rule
 * public final ProvideSystemProperty properties = ProvideSystemProperty
 * 		.fromResource(&quot;example.properties&quot;);
 * </pre>
 */
public class ProvideSystemProperty extends ExternalResource {
	private final Map<String, String> properties = new LinkedHashMap<String, String>();
	private final RestoreSpecificSystemProperties restoreSystemProperty = new RestoreSpecificSystemProperties();

	public static ProvideSystemProperty fromFile(String name)
		throws IOException {
		FileInputStream fis = new FileInputStream(name);
		return fromInputStream(fis);
	}

	public static ProvideSystemProperty fromResource(String name)
		throws IOException {
		InputStream is = ProvideSystemProperty.class.getResourceAsStream(name);
		return fromInputStream(is);
	}

	private static ProvideSystemProperty fromInputStream(InputStream is)
		throws IOException {
		Properties p = new Properties();
		p.load(is);
		ProvideSystemProperty rule = new ProvideSystemProperty();
		for (Map.Entry<Object, Object> property : p.entrySet())
			rule.addProperty((String) property.getKey(),
				(String) property.getValue());
		return rule;
	}

	public ProvideSystemProperty() {
	}

	/**
	 * Sets the property with the name to the specified value. After the test
	 * the rule restores the value of the property at the point of setting it.
	 *
	 * @param name the name of the property.
	 * @param value the new value of the property.
	 * @since 1.6.0
	 */
	public void setProperty(String name, String value) {
		restoreSystemProperty.add(name);
		if (value == null)
			clearProperty(name);
		else
			System.setProperty(name, value);
	}

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
		setProperties();
	}

	private void setProperties() {
		for (Entry<String, String> property : properties.entrySet()) {
			String name = property.getKey();
			String value = property.getValue();
			setProperty(name, value);
		}
	}

	@Override
	protected void after() {
		restoreSystemProperty.restore();
	}
}
