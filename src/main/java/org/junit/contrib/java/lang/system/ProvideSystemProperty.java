package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.setProperty;

import org.junit.rules.ExternalResource;

/**
 * The {@code ProvideSystemProperty} rule provides an arbitrary value for a
 * system property to a test. After the test the original value is restored. You
 * can ensure that a property is not set by providing {@code null}.
 * 
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
 */
public class ProvideSystemProperty extends ExternalResource {
	private final RestoreSystemProperties restoreSystemProperty;
	private final String name;
	private final String value;

	public ProvideSystemProperty(String name, String value) {
		this.name = name;
		this.value = value;
		this.restoreSystemProperty = new RestoreSystemProperties(name);
	}

	@Override
	protected void before() throws Throwable {
		restoreSystemProperty.before();
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
