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
		applyRule();
		assertThat(getProperty(SECOND_ARBITRARY_NAME),
				is(equalTo(ARBITRARY_VALUE)));
	}

	@Test
	public void originallyUnsetPropertyRemainsUnset() throws Throwable {
		clearProperty(SECOND_ARBITRARY_NAME);
		applyRule();
		assertThat(getProperty(SECOND_ARBITRARY_NAME),
				is(nullValue(String.class)));
	}

	private void applyRule() throws Throwable {
		rule.apply(new ClearedValue(), null).evaluate();
	}

	private class ClearedValue extends Statement {

		@Override
		public void evaluate() throws Throwable {
			assertThat(getProperty(SECOND_ARBITRARY_NAME),
					is(nullValue(String.class)));
		}
	}
}
