package org.junit.contrib.java.lang.system;

import static java.lang.System.out;
import static java.lang.System.setOut;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

public class StandardOutputStreamLogTest {
	private static final String ARBITRARY_TEXT = "arbitrary text";

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void logWriting() throws Throwable {
		StandardOutputStreamLog log = createLogWithoutSpecificMode();
		executeRuleWithStatement(log, new WriteTextToStandardOutputStream());
		assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
	}

	@Test
	public void restoreSystemOutputStream() throws Throwable {
		StandardOutputStreamLog log = createLogWithoutSpecificMode();
		PrintStream originalStream = out;
		executeRuleWithStatement(log, new WriteTextToStandardOutputStream());
		assertThat(originalStream, is(sameInstance(out)));
	}

	@Test
	public void stillWritesToSystemOutputStreamIfNoLogModeHasBeenSpecified() throws Throwable {
		StandardOutputStreamLog log = createLogWithoutSpecificMode();
		PrintStream originalStream = out;
		try {
			ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
			executeRuleWithStatement(log, new WriteTextToStandardOutputStream());
			assertThat(captureOutputStream,
					hasToString(equalTo(ARBITRARY_TEXT)));
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void doesNotWriteToSystemOutputStreamForLogOnlyMode() throws Throwable {
		StandardOutputStreamLog log = new StandardOutputStreamLog(LogMode.LOG_ONLY);
		PrintStream originalStream = out;
		try {
			ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
			executeRuleWithStatement(log, new WriteTextToStandardOutputStream());
			assertThat(captureOutputStream, hasToString(isEmptyString()));
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void collectsLogAfterClearing() throws Throwable {
		StandardOutputStreamLog log = createLogWithoutSpecificMode();
		executeRuleWithStatement(log, new ClearLogWhileWritingTextToStandardOutputStream(log));
		assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
	}

	@Test
	public void cannotBeCreatedWithoutLogMode() {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage(equalTo("The LogMode is missing."));
		new StandardErrorStreamLog(null);
	}

	private StandardOutputStreamLog createLogWithoutSpecificMode() {
		return new StandardOutputStreamLog();
	}

	private void executeRuleWithStatement(TestRule rule, Statement statement) throws Throwable {
		rule.apply(statement, null).evaluate();
	}

	private class WriteTextToStandardOutputStream extends Statement {
		@Override
		public void evaluate() throws Throwable {
			out.print(ARBITRARY_TEXT);
		}
	}

	private class ClearLogWhileWritingTextToStandardOutputStream extends
			Statement {
		private final StandardOutputStreamLog log;

		private ClearLogWhileWritingTextToStandardOutputStream(StandardOutputStreamLog log) {
			this.log = log;
		}

		@Override
		public void evaluate() throws Throwable {
			out.print(ARBITRARY_TEXT);
			log.clear();
			out.print(ARBITRARY_TEXT);
		}
	}
}
