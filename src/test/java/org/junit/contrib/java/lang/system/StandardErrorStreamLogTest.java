package org.junit.contrib.java.lang.system;

import static java.lang.System.err;
import static java.lang.System.setErr;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.junit.runners.model.Statement;

public class StandardErrorStreamLogTest {
	private static final String ARBITRARY_TEXT = "arbitrary text";

	private final StandardErrorStreamLog log = new StandardErrorStreamLog();

	@Test
	public void logWriting() throws Throwable {
		executeRuleWithStatement(new WriteTextToStandardOutputStream());
		assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
	}

	@Test
	public void restoreSystemErrorStream() throws Throwable {
		PrintStream originalStream = err;
		executeRuleWithStatement(new WriteTextToStandardOutputStream());
		assertThat(originalStream, is(sameInstance(err)));
	}

	@Test
	public void stillWritesToSystemErrorStream() throws Throwable {
		PrintStream originalStream = err;
		try {
			ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
			executeRuleWithStatement(new WriteTextToStandardOutputStream());
			assertThat(captureErrorStream, hasToString(equalTo(ARBITRARY_TEXT)));
		} finally {
			setErr(originalStream);
		}
	}

	@Test
	public void collectsLogAfterClearing() throws Throwable {
		executeRuleWithStatement(new ClearLogWhileWritingTextToStandardOutputStream());
		assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
	}

	private void executeRuleWithStatement(Statement statement) throws Throwable {
		log.apply(statement, null).evaluate();
	}

	private class WriteTextToStandardOutputStream extends Statement {
		@Override
		public void evaluate() throws Throwable {
			err.print(ARBITRARY_TEXT);
		}
	}

	private class ClearLogWhileWritingTextToStandardOutputStream extends
			Statement {
		@Override
		public void evaluate() throws Throwable {
			err.print(ARBITRARY_TEXT);
			log.clear();
			err.print(ARBITRARY_TEXT);
		}
	}
}
