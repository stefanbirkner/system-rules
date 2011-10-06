package org.junit.contrib.java.lang.system;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runners.model.Statement;

public class TextFromStandardInputStreamTest {
	private static final String ARBITRARY_TEXT = "arbitrary text";

	@Rule
	public final Timeout timeout = new Timeout(1000);

	private final TextFromStandardInputStream rule = new TextFromStandardInputStream(
			ARBITRARY_TEXT);

	@Test
	public void provideText() throws Throwable {
		ReadTextFromSystemIn statement = new ReadTextFromSystemIn();
		executeRuleWithStatement(statement);
		assertThat(statement.textFromSystemIn, is(equalTo(ARBITRARY_TEXT)));
	}

	@Test
	public void restoreSystemIn() throws Throwable {
		InputStream originalSystemIn = System.in;
		executeRuleWithStatement(new EmptyStatement());
		assertThat(System.in, is(equalTo(originalSystemIn)));
	}

	private void executeRuleWithStatement(Statement statement) throws Throwable {
		rule.apply(statement, null).evaluate();
	}

	private class ReadTextFromSystemIn extends Statement {
		private String textFromSystemIn;

		@Override
		public void evaluate() throws Throwable {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			textFromSystemIn = reader.readLine();
		}
	}
}
