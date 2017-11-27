package org.junit.contrib.java.lang.system;

import static java.lang.System.*;
import static org.apache.commons.io.IOUtils.copy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.ProvideSystemProperty.fromFile;
import static org.junit.contrib.java.lang.system.ProvideSystemProperty.fromResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

@RunWith(Enclosed.class)
public class ProvideSystemPropertyTest {
	private static final String EXAMPLE_PROPERTIES = "example.properties";
	private static final String ARBITRARY_KEY = "arbitrary property";
	private static final String ANOTHER_KEY = "another property";
	private static final String ARBITRARY_VALUE = "arbitrary value";
	private static final String A_DIFFERENT_VALUE = "different value";

	@RunWith(AcceptanceTestRunner.class)
	public static class provided_property_values_are_present_during_test {
		@ClassRule
		public static final RestoreSystemProperties restoreSystemProperty
			= new RestoreSystemProperties();

		public static class TestClass {
			@Rule
			public final ProvideSystemProperty provideSystemProperty =
				new ProvideSystemProperty(ARBITRARY_KEY, ARBITRARY_VALUE)
					.and(ANOTHER_KEY, A_DIFFERENT_VALUE);

			@Test
			public void test() {
				assertThat(getProperty(ARBITRARY_KEY)).isEqualTo(ARBITRARY_VALUE);
				assertThat(getProperty(ANOTHER_KEY)).isEqualTo(A_DIFFERENT_VALUE);
			}
		}

		public static void verifyResult(Collection<Failure> failures) {
			assertThat(failures).isEmpty();
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class property_is_null_during_test_if_set_to_null {
		@ClassRule
		public static final RestoreSystemProperties restoreSystemProperty
			= new RestoreSystemProperties();

		@BeforeClass
		public static void populateProperty() {
			setProperty(ARBITRARY_KEY, "value before executing the rule");
		}

		public static class TestClass {
			@Rule
			public final ProvideSystemProperty provideSystemProperty =
				new ProvideSystemProperty(ARBITRARY_KEY, null);

			@Test
			public void test() {
				assertThat(getProperty(ARBITRARY_KEY)).isNull();
			}
		}

		public static void verifyResult(Collection<Failure> failures) {
			assertThat(failures).isEmpty();
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class after_test_properties_have_the_same_values_as_before {
		@ClassRule
		public static final RestoreSystemProperties restoreSystemProperty
			= new RestoreSystemProperties();

		@BeforeClass
		public static void populateProperties() {
			setProperty(ARBITRARY_KEY, "value of first property");
			setProperty(ANOTHER_KEY, "value of second property");
		}

		public static class TestClass {
			@Rule
			public final ProvideSystemProperty provideSystemProperty =
				new ProvideSystemProperty(ARBITRARY_KEY, "different value")
					.and(ANOTHER_KEY, "another different value");

			@Test
			public void test() {
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(getProperty(ARBITRARY_KEY))
				.isEqualTo("value of first property");
			assertThat(getProperty(ANOTHER_KEY))
				.isEqualTo("value of second property");
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class property_that_does_not_exist_before_the_test_does_not_exist_after_the_test {
		@ClassRule
		public static final RestoreSystemProperties restoreSystemProperty
			= new RestoreSystemProperties();

		@BeforeClass
		public static void ensurePropertyIsMissing() {
			clearProperty(ARBITRARY_KEY);
		}

		public static class TestClass {
			@Rule
			public final ProvideSystemProperty provideSystemProperty =
				new ProvideSystemProperty(ARBITRARY_KEY, "other value");

			@Test
			public void test() {
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(getProperty(ARBITRARY_KEY)).isNull();
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class properties_from_resource_are_present_during_test {
		@ClassRule
		public static final RestoreSystemProperties restoreSystemProperty
			= new RestoreSystemProperties();

		public static class TestClass {
			@Rule
			public final ProvideSystemProperty provideSystemProperty = fromResource(EXAMPLE_PROPERTIES);

			@Test
			public void test() {
				assertThat(getProperty(ARBITRARY_KEY)).isEqualTo(ARBITRARY_VALUE);
			}
		}

		public static void verifyResult(Collection<Failure> failures) {
			assertThat(failures).isEmpty();
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class properties_from_file_are_present_during_test {
		@ClassRule
		public static final RestoreSystemProperties restoreSystemProperty
			= new RestoreSystemProperties();

		@ClassRule
		public static final TemporaryFolder temporaryFolder = new TemporaryFolder();

		@BeforeClass
		public static void createFile() throws Exception {
			file = temporaryFolder.newFile();
			copyResourceToFile(EXAMPLE_PROPERTIES, file);
		}

		private static void copyResourceToFile(String name, File file) throws Exception {
			FileOutputStream fos = new FileOutputStream(file);
			InputStream is = ProvideSystemPropertyTest.class.getResourceAsStream(name);
			copy(is, fos);
		}

		private static File file;

		public static class TestClass {
			@Rule
			public final ProvideSystemProperty provideSystemProperty = fromFile(file.getAbsolutePath());

			@Test
			public void test() {
				assertThat(getProperty(ARBITRARY_KEY)).isEqualTo(ARBITRARY_VALUE);
			}
		}

		public static void verifyResult(Collection<Failure> failures) {
			assertThat(failures).isEmpty();
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class property_has_value_that_is_set_within_the_test_using_the_rule {
		@ClassRule
		public static final RestoreSystemProperties restoreSystemProperty
			= new RestoreSystemProperties();

		@BeforeClass
		public static void populateProperty() {
			setProperty(ARBITRARY_KEY, "value before executing the rule");
		}

		public static class TestClass {
			@Rule
			public final ProvideSystemProperty provideSystemProperty = new ProvideSystemProperty();

			@Test
			public void test() {
				setProperty(ARBITRARY_KEY, "dummy value");
				assertThat(getProperty(ARBITRARY_KEY)).isEqualTo("dummy value");
			}
		}

		public static void verifyResult(Collection<Failure> failures) {
			assertThat(failures).isEmpty();
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class after_test_property_has_the_same_values_as_before_if_set_within_test_using_the_rule {
		@ClassRule
		public static final RestoreSystemProperties restoreSystemProperty
			= new RestoreSystemProperties();

		@BeforeClass
		public static void populateProperty() {
			setProperty(ARBITRARY_KEY, "value before executing the rule");
		}

		public static class TestClass {
			@Rule
			public final ProvideSystemProperty provideSystemProperty = new ProvideSystemProperty();

			@Test
			public void test() {
				provideSystemProperty.setProperty(ARBITRARY_KEY, "dummy value");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(getProperty(ARBITRARY_KEY))
				.isEqualTo("value before executing the rule");
		}
	}
}
