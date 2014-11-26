package org.junit.contrib.java.lang.system;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RestoreSystemPropertiesTest {
	private static final Description NO_DESCRIPTION = null;
	private static final String FIRST_PROPERTY = "first property";
	private static final String SECOND_PROPERTY = "second property";
	private static final String ARBITRARY_VALUE = "arbitrary value";
	private static final String VALUE_SET_BY_STATEMENT = "value set by statement";

	private final RestoreSystemProperties rule = new RestoreSystemProperties(
		FIRST_PROPERTY, SECOND_PROPERTY);

	@Test
	public void restoresFirstPropertySpecifiedByConstructor() throws Throwable {
		String originalValue = getProperty(FIRST_PROPERTY);
		Statement setValueOfProperty = new SetValueOfProperty(FIRST_PROPERTY);
		rule.apply(setValueOfProperty, NO_DESCRIPTION).evaluate();
		assertThat(getProperty(FIRST_PROPERTY), is(equalTo(originalValue)));
	}

	@Test
	public void restoresSecondPropertySpecifiedByConstructor() throws Throwable {
		String originalValue = getProperty(SECOND_PROPERTY);
		Statement setValueOfProperty = new SetValueOfProperty(SECOND_PROPERTY);
		rule.apply(setValueOfProperty, NO_DESCRIPTION).evaluate();
		assertThat(getProperty(SECOND_PROPERTY), is(equalTo(originalValue)));
	}

	@Test
	public void restoreExistingProperty() throws Throwable {
		setProperty(FIRST_PROPERTY, ARBITRARY_VALUE);
		Statement setValueOfProperty = new SetValueOfProperty(FIRST_PROPERTY);
		rule.apply(setValueOfProperty, NO_DESCRIPTION).evaluate();
		assertThat(getProperty(FIRST_PROPERTY), is(equalTo(ARBITRARY_VALUE)));
	}

	@Test
	public void restoresPropertyThatHasBeenAddedByTheStatement()
		throws Throwable {
		String originalValue = getProperty("property added by statement");
		Statement addPropertyAndSetValueOfProperty = new AddPropertyAndSetValueOfProperty(
			"property added by statement");
		rule.apply(addPropertyAndSetValueOfProperty, NO_DESCRIPTION).evaluate();
		assertThat(getProperty("property added by statement"),
			is(equalTo(originalValue)));
	}

	private class SetValueOfProperty extends Statement {
		private final String name;

		SetValueOfProperty(String name) {
			this.name = name;
		}

		@Override
		public void evaluate() throws Throwable {
			setProperty(name, VALUE_SET_BY_STATEMENT);
		}
	}

	private class AddPropertyAndSetValueOfProperty extends Statement {
		private final String name;

		AddPropertyAndSetValueOfProperty(String name) {
			this.name = name;
		}

		@Override
		public void evaluate() throws Throwable {
			rule.add(name);
			setProperty(name, VALUE_SET_BY_STATEMENT);
		}
	}
}
