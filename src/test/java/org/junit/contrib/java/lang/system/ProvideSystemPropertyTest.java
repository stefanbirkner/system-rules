package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;

public class ProvideSystemPropertyTest {
	private static final String ARBITRARY_NAME = "arbitrary property";
	private static final String ANOTHER_PROPERTY = "another property";
	private static final String ARBITRARY_VALUE = "arbitrary value";
	private static final String A_DIFFERENT_VALUE = "different value";

	private ProvideSystemProperty rule;

	@Rule
	public final RestoreSystemProperties restoreSystemProperty = new RestoreSystemProperties(ARBITRARY_NAME,
			ANOTHER_PROPERTY);

	@Test
	public void removeProperty() throws Throwable {
		setProperty(ARBITRARY_NAME, ARBITRARY_VALUE);
		AssertValue assertValue = new AssertValue(ARBITRARY_NAME, null);
		rule = new ProvideSystemProperty(ARBITRARY_NAME, null);
		rule.apply(assertValue, null).evaluate();
	}

	@Test
	public void restoreOriginalValue() throws Throwable {
		setProperty(ARBITRARY_NAME, A_DIFFERENT_VALUE);
		evaluateStatementWithArbitraryValue();
		assertThat(getProperty(ARBITRARY_NAME), is(equalTo(A_DIFFERENT_VALUE)));
	}

	@Test
	public void removeValueIfNotPresentBefore() throws Throwable {
		clearProperty(ARBITRARY_NAME);
		evaluateStatementWithArbitraryValue();
		assertThat(getProperty(ARBITRARY_NAME), is(nullValue()));
	}

	@Test
	public void providesMultipleProperties() throws Throwable {
		rule = new ProvideSystemProperty(ARBITRARY_NAME, ARBITRARY_VALUE).and(ANOTHER_PROPERTY, A_DIFFERENT_VALUE);
		AssertValue assertValue = new AssertValue(ARBITRARY_NAME, ARBITRARY_VALUE);
		rule.apply(assertValue, null).evaluate();
		assertValue = new AssertValue(ANOTHER_PROPERTY, A_DIFFERENT_VALUE);
		rule.apply(assertValue, null).evaluate();
	}

	@Test
	public void restoresMultipleProperties() throws Throwable {
		setProperty(ANOTHER_PROPERTY, ARBITRARY_VALUE);

		rule = new ProvideSystemProperty(ARBITRARY_NAME, ARBITRARY_VALUE).and(ANOTHER_PROPERTY, A_DIFFERENT_VALUE);
		AssertValue assertValue = new AssertValue(ANOTHER_PROPERTY, A_DIFFERENT_VALUE);
		rule.apply(assertValue, null).evaluate();
		
		assertThat(getProperty(ARBITRARY_NAME), is(nullValue()));
		assertThat(getProperty(ANOTHER_PROPERTY), is(ARBITRARY_VALUE));
	}

	private void evaluateStatementWithArbitraryValue() throws Throwable {
		AssertValue assertValue = new AssertValue(ARBITRARY_NAME, ARBITRARY_VALUE);
		rule = new ProvideSystemProperty(ARBITRARY_NAME, ARBITRARY_VALUE);
		rule.apply(assertValue, null).evaluate();
	}

	class AssertValue extends Statement {
		final String name;
		final String value;

		AssertValue(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void evaluate() {
			assertThat(getProperty(name), is(equalTo(value)));
		}
	}
}
