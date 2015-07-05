package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.apache.commons.io.IOUtils.copy;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.contrib.java.lang.system.Matchers.hasPropertyWithValue;
import static org.junit.contrib.java.lang.system.Matchers.notHasProperty;
import static org.junit.contrib.java.lang.system.ProvideSystemProperty.fromFile;
import static org.junit.contrib.java.lang.system.ProvideSystemProperty.fromResource;
import static org.junit.contrib.java.lang.system.Statements.TEST_THAT_DOES_NOTHING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ProvideSystemPropertyTest {
	private static final String EXAMPLE_PROPERTIES = "example.properties";
	private static final Description NO_DESCRIPTION = null;
	private static final String ARBITRARY_KEY = "arbitrary property";
	private static final String ANOTHER_KEY = "another property";
	private static final String ARBITRARY_VALUE = "arbitrary value";
	private static final String A_DIFFERENT_VALUE = "different value";

	private ProvideSystemProperty rule;

	@Rule
	public final RestoreSystemProperties restoreSystemProperty
		= new RestoreSystemProperties();

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void provided_property_values_are_present_during_test()
			throws Throwable {
		rule = new ProvideSystemProperty(ARBITRARY_KEY, ARBITRARY_VALUE)
			.and(ANOTHER_KEY, A_DIFFERENT_VALUE);
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		evaluateRuleForStatement(test);
		assertThat(test.propertiesAtStart, allOf(
			hasPropertyWithValue(ARBITRARY_KEY, ARBITRARY_VALUE),
			hasPropertyWithValue(ANOTHER_KEY, A_DIFFERENT_VALUE)));
	}

	@Test
	public void property_is_null_during_test_if_set_to_null() throws Throwable {
		setProperty(ARBITRARY_KEY, ARBITRARY_VALUE);
		rule = new ProvideSystemProperty(ARBITRARY_KEY, null);
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		evaluateRuleForStatement(test);
		assertThat(test.propertiesAtStart, notHasProperty(ARBITRARY_KEY));
	}

	@Test
	public void after_test_properties_have_the_same_values_as_before() throws Throwable {
		setProperty(ARBITRARY_KEY, "value of first property");
		setProperty(ANOTHER_KEY, "value of second property");
		rule = new ProvideSystemProperty(ARBITRARY_KEY, "different value")
			.and(ANOTHER_KEY, "another different value");
		evaluateRuleForStatement(TEST_THAT_DOES_NOTHING);
		assertThat(getProperty(ARBITRARY_KEY),
			is(equalTo("value of first property")));
		assertThat(getProperty(ANOTHER_KEY),
			is(equalTo("value of second property")));
	}

	@Test
	public void property_that_does_not_exist_before_the_test_does_not_exist_after_the_test()
			throws Throwable {
		clearProperty(ARBITRARY_KEY);
		rule = new ProvideSystemProperty(ARBITRARY_KEY, "other value");
		evaluateRuleForStatement(TEST_THAT_DOES_NOTHING);
		assertThat(getProperty(ARBITRARY_KEY), is(nullValue()));
	}

	@Test
	public void properties_from_resource_are_present_during_test() throws Throwable {
		rule = fromResource(EXAMPLE_PROPERTIES);
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		evaluateRuleForStatement(test);
		assertThat(test.propertiesAtStart,
			hasPropertyWithValue(ARBITRARY_KEY, ARBITRARY_VALUE));
	}

	@Test
	public void properties_from_file_are_present_during_test() throws Throwable {
		File file = temporaryFolder.newFile();
		copyResourceToFile(EXAMPLE_PROPERTIES, file);
		rule = fromFile(file.getAbsolutePath());
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		evaluateRuleForStatement(test);
		assertThat(test.propertiesAtStart,
			hasPropertyWithValue(ARBITRARY_KEY, ARBITRARY_VALUE));
	}

	@Test
	public void property_has_value_that_is_set_within_the_test_using_the_rule()
			throws Throwable {
		setProperty(ARBITRARY_KEY, "value before executing the rule");
		rule = new ProvideSystemProperty();
		evaluateRuleForStatement(new SetPropertyAndAssertValue(ARBITRARY_KEY,
			"dummy value"));
	}

	@Test
	public void after_test_property_has_the_same_values_as_before_if_set_within_test_using_the_rule()
			throws Throwable {
		setProperty(ARBITRARY_KEY, "value before executing the rule");
		rule = new ProvideSystemProperty();
		evaluateRuleForStatement(
			new SetProperty(ARBITRARY_KEY, "dummy value"));
		assertThat(getProperty(ARBITRARY_KEY),
			is(equalTo("value before executing the rule")));
	}

	private void evaluateRuleForStatement(Statement statement) throws Throwable {
		Statement ruleWithStatement = rule.apply(statement, NO_DESCRIPTION);
		ruleWithStatement.evaluate();
	}

	private void copyResourceToFile(String name, File file) throws Exception {
		FileOutputStream fos = new FileOutputStream(file);
		InputStream is = getClass().getResourceAsStream(name);
		copy(is, fos);
	}

	private class SetPropertyAndAssertValue extends Statement {
		final String name;
		final String value;

		SetPropertyAndAssertValue(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void evaluate() {
			rule.setProperty(name, value);
			assertThat(getProperty(name), is(equalTo(value)));
		}
	}

	private class SetProperty extends Statement {
		final String name;
		final String value;

		SetProperty(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void evaluate() {
			rule.setProperty(name, value);
		}
	}
}
