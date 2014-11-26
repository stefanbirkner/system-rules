package org.junit.contrib.java.lang.system;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

import java.io.InputStream;
import java.util.Scanner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runners.model.Statement;

public class TextFromStandardInputStreamTest {
	private static final String ARBITRARY_TEXT = "arbitrary text";

	@Rule
	public final Timeout timeout = new Timeout(1000);

	private final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

	@Test
	public void provideText() throws Throwable {
		ReadTextFromSystemIn statement = new ReadTextFromSystemIn(ARBITRARY_TEXT);
		executeRuleWithStatement(statement);
		assertThat(statement.textFromSystemIn, is(equalTo(ARBITRARY_TEXT)));
	}

	@Test
	public void restoreSystemIn() throws Throwable {
		InputStream originalSystemIn = System.in;
		executeRuleWithStatement(new EmptyStatement());
		assertThat(System.in, is(sameInstance(originalSystemIn)));
	}

	private void executeRuleWithStatement(Statement statement) throws Throwable {
		systemInMock.apply(statement, null).evaluate();
	}

	private class ReadTextFromSystemIn extends Statement {
		private final String textProvidedBySystemIn;
		private String textFromSystemIn;

		public ReadTextFromSystemIn(String textProvidedBySystemIn) {
			this.textProvidedBySystemIn = textProvidedBySystemIn;
		}

		@Override
		public void evaluate() throws Throwable {
			systemInMock.provideText(textProvidedBySystemIn);
			Scanner scanner = new Scanner(System.in);
			textFromSystemIn = scanner.nextLine();
		}
	}
}
