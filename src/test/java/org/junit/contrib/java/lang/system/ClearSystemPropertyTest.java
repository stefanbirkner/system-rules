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

public class ClearSystemPropertyTest {
	
	private static final String ARBITRARY_NAME = "arbitrary property";
	private static final String ARBITRARY_VALUE = "arbitrary value";

	@Rule
	public final RestoreSystemProperties restore = new RestoreSystemProperties(
			ARBITRARY_NAME);

	@Test
	public void restoresOriginalValue() throws Throwable {
		setProperty(ARBITRARY_NAME, ARBITRARY_VALUE);
		
		ClearSystemProperty rule = new ClearSystemProperty(ARBITRARY_NAME);
		rule.apply(new ClearedValue(), null).evaluate();
		
		assertThat(getProperty(ARBITRARY_NAME), is(equalTo(ARBITRARY_VALUE)));
	}

	@Test
	public void originallyUnsetPropertyRemainsUnset() throws Throwable {
		clearProperty(ARBITRARY_NAME);
		
		ClearSystemProperty rule = new ClearSystemProperty(ARBITRARY_NAME);
		rule.apply(new ClearedValue(), null).evaluate();
		
		assertThat(getProperty(ARBITRARY_NAME), is(nullValue(String.class)));
	}

	private class ClearedValue extends Statement {

		@Override
		public void evaluate() throws Throwable {
			assertThat(getProperty(ARBITRARY_NAME), is(nullValue(String.class)));
		}
	}
}
