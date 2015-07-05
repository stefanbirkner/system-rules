package org.junit.contrib.java.lang.system;

import static java.lang.System.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.contrib.java.lang.system.Matchers.hasPropertyWithValue;
import static org.junit.contrib.java.lang.system.Matchers.notHasProperty;
import static org.junit.contrib.java.lang.system.Statements.SUCCESSFUL_TEST;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.Map;

public class ClearSystemPropertiesTest {
	private static final Description DUMMY_DESCRIPTION = null;

	@Rule
	public final RestoreSystemProperties restore = new RestoreSystemProperties();

	@Test
	public void properties_are_cleared_at_start_of_test() {
		setProperty("first property", "dummy value");
		setProperty("second property", "another dummy value");
		ClearSystemProperties rule = new ClearSystemProperties(
			"first property", "second property");
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		runTestWithRule(test, rule);
		assertThat(test.propertiesAtStart, allOf(
			notHasProperty("first property"),
			notHasProperty("second property")));
	}

	@Test
	public void property_is_cleared_after_added_to_rule_within_test() {
		setProperty("property", "dummy value");
		ClearSystemProperties rule = new ClearSystemProperties();
		TestThatAddsProperty test = new TestThatAddsProperty("property", rule);
		runTestWithRule(test, rule);
		assertThat(test.propertiesAfterAddingProperty,
			notHasProperty("property"));
	}

	@Test
	public void after_test_properties_have_the_same_values_as_before() {
		setProperty("first property", "dummy value");
		setProperty("second property", "another dummy value");
		setProperty("third property", "another dummy value");
		ClearSystemProperties rule = new ClearSystemProperties(
			"first property", "second property");
		runTestWithRule(new TestThatAddsProperty("third property", rule), rule);
		assertThat(getProperties(), allOf(
			hasPropertyWithValue("first property", "dummy value"),
			hasPropertyWithValue("second property", "another dummy value"),
			hasPropertyWithValue("third property", "another dummy value")));
	}

	@Test
	public void property_that_is_not_present_does_not_cause_failure() {
		clearProperty("property");
		ClearSystemProperties rule = new ClearSystemProperties("property");
		runTestWithRule(SUCCESSFUL_TEST, rule);
		//everything is fine if no exception is thrown
	}

	private void runTestWithRule(Statement test, ClearSystemProperties rule) {
		rule.apply(test, DUMMY_DESCRIPTION);
	}

	private class TestThatAddsProperty extends Statement {
		private final String property;
		private ClearSystemProperties rule;
		Map<Object, Object> propertiesAfterAddingProperty;

		TestThatAddsProperty(String property, ClearSystemProperties rule) {
			this.property = property;
			this.rule = rule;
		}

		@Override
		public void evaluate() throws Throwable {
			rule.clearProperty(property);
			propertiesAfterAddingProperty = getProperties();
		}
	}
}
