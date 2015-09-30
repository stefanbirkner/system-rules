package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.apache.commons.io.IOUtils.copy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.ProvideSystemProperty.fromFile;
import static org.junit.contrib.java.lang.system.ProvideSystemProperty.fromResource;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;
import static org.junit.contrib.java.lang.system.Statements.TEST_THAT_DOES_NOTHING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.model.Statement;

public class ProvideSystemPropertyTest {
	private static final String EXAMPLE_PROPERTIES = "example.properties";
	private static final String ARBITRARY_KEY = "arbitrary property";
	private static final String ANOTHER_KEY = "another property";
	private static final String ARBITRARY_VALUE = "arbitrary value";
	private static final String A_DIFFERENT_VALUE = "different value";

	@Rule
	public final RestoreSystemProperties restoreSystemProperty
		= new RestoreSystemProperties();

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void provided_property_values_are_present_during_test() {
		ProvideSystemProperty rule = new ProvideSystemProperty(
			ARBITRARY_KEY, ARBITRARY_VALUE)
			.and(ANOTHER_KEY, A_DIFFERENT_VALUE);
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		executeTestWithRule(test, rule);
		assertThat(test.propertiesAtStart)
			.containsEntry(ARBITRARY_KEY, ARBITRARY_VALUE)
			.containsEntry(ANOTHER_KEY, A_DIFFERENT_VALUE);
	}

	@Test
	public void property_is_null_during_test_if_set_to_null() {
		setProperty(ARBITRARY_KEY, ARBITRARY_VALUE);
		ProvideSystemProperty rule = new ProvideSystemProperty(
			ARBITRARY_KEY, null);
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		executeTestWithRule(test, rule);
		assertThat(test.propertiesAtStart).doesNotContainKey(ARBITRARY_KEY);
	}

	@Test
	public void after_test_properties_have_the_same_values_as_before() {
		setProperty(ARBITRARY_KEY, "value of first property");
		setProperty(ANOTHER_KEY, "value of second property");
		ProvideSystemProperty rule = new ProvideSystemProperty(
			ARBITRARY_KEY, "different value")
			.and(ANOTHER_KEY, "another different value");
		executeTestWithRule(TEST_THAT_DOES_NOTHING, rule);
		assertThat(getProperty(ARBITRARY_KEY))
			.isEqualTo("value of first property");
		assertThat(getProperty(ANOTHER_KEY))
			.isEqualTo("value of second property");
	}

	@Test
	public void property_that_does_not_exist_before_the_test_does_not_exist_after_the_test() {
		clearProperty(ARBITRARY_KEY);
		ProvideSystemProperty rule = new ProvideSystemProperty(
			ARBITRARY_KEY, "other value");
		executeTestWithRule(TEST_THAT_DOES_NOTHING, rule);
		assertThat(getProperty(ARBITRARY_KEY)).isNull();
	}

	@Test
	public void properties_from_resource_are_present_during_test() throws Exception {
		ProvideSystemProperty rule = fromResource(EXAMPLE_PROPERTIES);
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		executeTestWithRule(test, rule);
		assertThat(test.propertiesAtStart)
			.containsEntry(ARBITRARY_KEY, ARBITRARY_VALUE);
	}

	@Test
	public void properties_from_file_are_present_during_test() throws Exception {
		File file = temporaryFolder.newFile();
		copyResourceToFile(EXAMPLE_PROPERTIES, file);
		ProvideSystemProperty rule = fromFile(file.getAbsolutePath());
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		executeTestWithRule(test, rule);
		assertThat(test.propertiesAtStart)
			.containsEntry(ARBITRARY_KEY, ARBITRARY_VALUE);
	}

	@Test
	public void property_has_value_that_is_set_within_the_test_using_the_rule() {
		setProperty(ARBITRARY_KEY, "value before executing the rule");
		ProvideSystemProperty rule = new ProvideSystemProperty();
		executeTestWithRule(
			new SetPropertyAndAssertValue(ARBITRARY_KEY, "dummy value", rule),
			rule);
	}

	@Test
	public void after_test_property_has_the_same_values_as_before_if_set_within_test_using_the_rule() {
		setProperty(ARBITRARY_KEY, "value before executing the rule");
		ProvideSystemProperty rule = new ProvideSystemProperty();
		executeTestWithRule(
			new SetProperty(ARBITRARY_KEY, "dummy value", rule), rule);
		assertThat(getProperty(ARBITRARY_KEY))
			.isEqualTo("value before executing the rule");
	}

	private void copyResourceToFile(String name, File file) throws Exception {
		FileOutputStream fos = new FileOutputStream(file);
		InputStream is = getClass().getResourceAsStream(name);
		copy(is, fos);
	}

	private class SetPropertyAndAssertValue extends Statement {
		final String name;
		final String value;
		final ProvideSystemProperty rule;

		SetPropertyAndAssertValue(String name, String value, ProvideSystemProperty rule) {
			this.name = name;
			this.value = value;
			this.rule = rule;
		}

		@Override
		public void evaluate() {
			rule.setProperty(name, value);
			assertThat(getProperty(name)).isEqualTo(value);
		}
	}

	private class SetProperty extends Statement {
		final String name;
		final String value;
		final ProvideSystemProperty rule;

		SetProperty(String name, String value, ProvideSystemProperty rule) {
			this.name = name;
			this.value = value;
			this.rule = rule;
		}

		@Override
		public void evaluate() {
			rule.setProperty(name, value);
		}
	}
}
