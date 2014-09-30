package org.junit.contrib.java.lang.system;

import static java.lang.System.err;
import static java.lang.System.setErr;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

public class StandardErrorStreamLogTest {
	private static final String ARBITRARY_TEXT = "arbitrary text";

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void logWriting() throws Throwable {
		StandardErrorStreamLog log = createLogWithoutSpecificMode();
		executeRuleWithStatement(log, new WriteTextToStandardOutputStream());
		assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
	}

	@Test
	public void restoreSystemErrorStream() throws Throwable {
		StandardErrorStreamLog log = createLogWithoutSpecificMode();
		PrintStream originalStream = err;
		executeRuleWithStatement(log, new WriteTextToStandardOutputStream());
		assertThat(originalStream, is(sameInstance(err)));
	}

	@Test
	public void stillWritesToSystemErrorStreamIfNoLogModeHasBeenSpecified() throws Throwable {
		StandardErrorStreamLog log = createLogWithoutSpecificMode();
		PrintStream originalStream = err;
		try {
			ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
			executeRuleWithStatement(log, new WriteTextToStandardOutputStream());
			assertThat(captureErrorStream, hasToString(equalTo(ARBITRARY_TEXT)));
		} finally {
			setErr(originalStream);
		}
	}

	@Test
	public void doesNotWriteToSystemErrorStreamForLogOnlyMode() throws Throwable {
		StandardErrorStreamLog log = new StandardErrorStreamLog(LogMode.LOG_ONLY);
		PrintStream originalStream = err;
		try {
			ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
			executeRuleWithStatement(log, new WriteTextToStandardOutputStream());
			assertThat(captureErrorStream, hasToString(isEmptyString()));
		} finally {
			setErr(originalStream);
		}
	}

	@Test
	public void collectsLogAfterClearing() throws Throwable {
		StandardErrorStreamLog log = createLogWithoutSpecificMode();
		executeRuleWithStatement(log, new ClearLogWhileWritingTextToStandardOutputStream(log));
		assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
	}

	@Test
	public void cannotBeCreatedWithoutLogMode() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage(equalTo("The LogMode is missing."));
		new StandardErrorStreamLog(null);
	}

	private StandardErrorStreamLog createLogWithoutSpecificMode() {
		return new StandardErrorStreamLog();
	}

	private void executeRuleWithStatement(TestRule rule, Statement statement) throws Throwable {
		rule.apply(statement, null).evaluate();
	}

	private class WriteTextToStandardOutputStream extends Statement {
		@Override
		public void evaluate() throws Throwable {
			err.print(ARBITRARY_TEXT);
		}
	}

	private class ClearLogWhileWritingTextToStandardOutputStream extends
			Statement {
		private final StandardErrorStreamLog log;

		private ClearLogWhileWritingTextToStandardOutputStream(StandardErrorStreamLog log) {
			this.log = log;
		}

		@Override
		public void evaluate() throws Throwable {
			err.print(ARBITRARY_TEXT);
			log.clear();
			err.print(ARBITRARY_TEXT);
		}
	}
}
