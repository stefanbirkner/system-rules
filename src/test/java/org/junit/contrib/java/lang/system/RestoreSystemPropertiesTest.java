package org.junit.contrib.java.lang.system;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.util.Collection;
import java.util.Properties;

@RunWith(Enclosed.class)
public class RestoreSystemPropertiesTest {
	//ensure that every test uses the same property, because this one is restored after the test
	private static final String PROPERTY_KEY = "dummy property";

	@RunWith(AcceptanceTestRunner.class)
	public static class after_test_properties_have_the_same_values_as_before {
		@BeforeClass
		public static void setProperty() {
			System.setProperty(PROPERTY_KEY, "dummy value");
		}

		public static class TestClass {
			@Rule
			public final TestRule restoreSystemProperties = new RestoreSystemProperties();

			@Test
			public void test() {
				System.setProperty(PROPERTY_KEY, "another value");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(System.getProperty(PROPERTY_KEY))
				.isEqualTo("dummy value");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class property_that_does_not_exist_before_the_test_does_not_exist_after_the_test {
		@BeforeClass
		public static void clearProperty() {
			System.clearProperty(PROPERTY_KEY);
		}

		public static class TestClass {
			@Rule
			public final TestRule restoreSystemProperties = new RestoreSystemProperties();

			@Test
			public void test() {
				System.setProperty(PROPERTY_KEY, "another value");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(System.getProperty(PROPERTY_KEY))
				.isNull();
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class at_start_of_a_test_properties_are_equal_to_the_original_properties {
		private static Properties originalProperties;

		@BeforeClass
		public static void changeAndCaptureOriginalProperties() {
			//ensure at least one property is set
			System.setProperty(PROPERTY_KEY, "dummy value");
			originalProperties = System.getProperties();
		}

		public static class TestClass {
			@Rule
			public final TestRule restoreSystemProperties = new RestoreSystemProperties();

			@Test
			public void test() {
				assertThat(System.getProperties())
					.isEqualTo(originalProperties);
			}
		}

		public static void verifyResult(Collection<Failure> failures) {
			assertThat(failures).isEmpty();
		}
	}
}
