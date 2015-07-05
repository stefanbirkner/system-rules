package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.contrib.java.lang.system.Matchers.hasPropertyWithValue;

import org.junit.After;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RestoreSystemPropertiesTest {
	private static final Description NO_DESCRIPTION = null;
	//ensure that every test uses the same property, because this one is restored after the test
	private static final String PROPERTY_KEY = "dummy property";

	private final String propertiesOriginalValue = getProperty(PROPERTY_KEY);
	private final TestRule rule = new RestoreSystemProperties();

	@After
	public void restoreProperty() {
		if (propertiesOriginalValue == null)
			clearProperty(PROPERTY_KEY);
		else
			System.setProperty(PROPERTY_KEY, propertiesOriginalValue);
	}

	@Test
	public void after_test_properties_have_the_same_values_as_before() throws Throwable {
		System.setProperty(PROPERTY_KEY, "dummy value");
		evaluateRuleForStatement(
			Statements.setProperty(PROPERTY_KEY, "another value"));
		assertThat(getProperty(PROPERTY_KEY), is(equalTo("dummy value")));
	}

	@Test
	public void property_that_does_not_exist_before_the_test_does_not_exist_after_the_test()
			throws Throwable {
		clearProperty(PROPERTY_KEY);
		evaluateRuleForStatement(
			Statements.setProperty(PROPERTY_KEY, "another value"));
		assertThat(getProperty(PROPERTY_KEY), is(nullValue()));
	}

	@Test
	public void property_value_is_unchanged_at_start_of_test()
			throws Throwable {
		System.setProperty(PROPERTY_KEY, "dummy value");
		TestThatCapturesProperties test = new TestThatCapturesProperties();
		evaluateRuleForStatement(test);
		assertThat(test.propertiesAtStart,
			hasPropertyWithValue(PROPERTY_KEY, "dummy value"));
	}

	private void evaluateRuleForStatement(Statement statement) throws Throwable {
		rule.apply(statement, NO_DESCRIPTION).evaluate();
	}
}
