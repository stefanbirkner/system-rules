package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
			setPropertyValue(propertiesOriginalValue);
	}

	@Test
	public void restoresExistingProperty() throws Throwable {
		setPropertyValue("dummy value");
		evaluateRuleThatWrapsStatementThatSetsThePropertyValue();
		assertThat(getPropertyValue(), is(equalTo("dummy value")));
	}

	@Test
	public void clearsMissingProperty() throws Throwable {
		clearProperty(PROPERTY_KEY);
		evaluateRuleThatWrapsStatementThatSetsThePropertyValue();
		assertThat(getPropertyValue(), is(nullValue()));
	}

	@Test
	public void providesPropertyToExecutedStatement() throws Throwable {
		setPropertyValue("dummy value");
		Statement verifyProperty = new Statement() {
			@Override
			public void evaluate() throws Throwable {
				assertThat(getPropertyValue(), is(equalTo("dummy value")));
			}
		};
		evaluateRuleThatWrapsStatement(verifyProperty);
	}

	private String getPropertyValue() {
		return getProperty("dummy property");
	}

	private void setPropertyValue(String value) {
		setProperty(PROPERTY_KEY, value);
	}

	private void evaluateRuleThatWrapsStatement(Statement setValueOfProperty) throws Throwable {
		rule.apply(setValueOfProperty, NO_DESCRIPTION).evaluate();
	}

	private void evaluateRuleThatWrapsStatementThatSetsThePropertyValue() throws Throwable {
		Statement setValueOfProperty = new SetValueOfProperty(PROPERTY_KEY);
		evaluateRuleThatWrapsStatement(setValueOfProperty);
	}

	private class SetValueOfProperty extends Statement {
		private final String name;

		SetValueOfProperty(String name) {
			this.name = name;
		}

		@Override
		public void evaluate() throws Throwable {
			setProperty(name, "value set by statement");
		}
	}
}
