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

public class ClearSystemPropertiesTest {
	private static final String FIRST_ARBITRARY_NAME = "first arbitrary property";
	private static final String SECOND_ARBITRARY_NAME = "second arbitrary property";
	private static final String ARBITRARY_VALUE = "arbitrary value";

	@Rule
	public final RestoreSystemProperties restore = new RestoreSystemProperties(
		SECOND_ARBITRARY_NAME);

	private final ClearSystemProperties rule = new ClearSystemProperties(
		FIRST_ARBITRARY_NAME, SECOND_ARBITRARY_NAME);

	@Test
	public void restoresOriginalValueOfSecondProperty() throws Throwable {
		setProperty(SECOND_ARBITRARY_NAME, ARBITRARY_VALUE);
		applyRuleToStatement(new VerifyValueIsCleared());
		assertThat(getProperty(SECOND_ARBITRARY_NAME),
			is(equalTo(ARBITRARY_VALUE)));
	}

	@Test
	public void originallyUnsetPropertyRemainsUnset() throws Throwable {
		clearProperty(SECOND_ARBITRARY_NAME);
		applyRuleToStatement(new VerifyValueIsCleared());
		assertThat(getProperty(SECOND_ARBITRARY_NAME),
			is(nullValue(String.class)));
	}

	@Test
	public void clearsPropertyDuringTestAndRestoresItAfterwards()
		throws Throwable {
		setProperty("another property", "dummy value");
		applyRuleToStatement(new ClearPropertyAndVerifyThatItIsCleared(
			"another property"));
		assertThat(getProperty("another property"), is("dummy value"));
	}

	private void applyRuleToStatement(Statement statement) throws Throwable {
		rule.apply(statement, null).evaluate();
	}

	private class VerifyValueIsCleared extends Statement {
		@Override
		public void evaluate() throws Throwable {
			assertThat(getProperty(SECOND_ARBITRARY_NAME),
				is(nullValue(String.class)));
		}
	}

	private class ClearPropertyAndVerifyThatItIsCleared extends Statement {
		private final String property;

		ClearPropertyAndVerifyThatItIsCleared(String property) {
			this.property = property;
		}

		@Override
		public void evaluate() throws Throwable {
			rule.clearProperty(property);
			assertThat(getProperty(property), is(nullValue(String.class)));
		}
	}
}
