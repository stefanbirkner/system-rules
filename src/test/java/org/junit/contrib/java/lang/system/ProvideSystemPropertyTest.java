package org.junit.contrib.java.lang.system;

import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;

public class ProvideSystemPropertyTest {
	private static final String ARBITRARY_NAME = "arbitrary property";
	private static final String ARBITRARY_VALUE = "arbitrary value";
	private static final String A_DIFFERENT_VALUE = "different value";

	@Rule
	public final RestoreSystemProperties restoreSystemProperty = new RestoreSystemProperties(
			ARBITRARY_NAME);

	@Test
	public void provideProperty() throws Throwable {
		evaluateStatementWithArbitraryValue();
	}

	@Test
	public void removeProperty() throws Throwable {
		setProperty(ARBITRARY_NAME, ARBITRARY_VALUE);
		AssertValue assertValue = new AssertValue(null);
		ProvideSystemProperty propertyNotPresent = new ProvideSystemProperty(
				ARBITRARY_NAME, null);
		propertyNotPresent.apply(assertValue, null).evaluate();
	}

	@Test
	public void restoreOriginalValue() throws Throwable {
		setProperty(ARBITRARY_NAME, A_DIFFERENT_VALUE);
		evaluateStatementWithArbitraryValue();
		assertThat(getProperty(ARBITRARY_NAME), is(equalTo(A_DIFFERENT_VALUE)));
		assertPropertyValue(is(equalTo(A_DIFFERENT_VALUE)));
	}

	@Test
	public void removeValueIfNotPresentBefore() throws Throwable {
		evaluateStatementWithArbitraryValue();
		assertThat(getProperty(ARBITRARY_NAME), is(nullValue()));
		assertPropertyValue(is(nullValue(String.class)));
	}

	private void evaluateStatementWithArbitraryValue() throws Throwable {
		AssertValue assertValue = new AssertValue(ARBITRARY_VALUE);
		ProvideSystemProperty rule = new ProvideSystemProperty(ARBITRARY_NAME,
				ARBITRARY_VALUE);
		rule.apply(assertValue, null).evaluate();
	}

	private void assertPropertyValue(Matcher<String> matcher) {
		assertThat(getProperty(ARBITRARY_NAME), matcher);
	}

	private class AssertValue extends Statement {
		private final String value;

		AssertValue(String value) {
			this.value = value;
		}

		@Override
		public void evaluate() throws Throwable {
			assertPropertyValue(is(equalTo(value)));
		}
	}
}
