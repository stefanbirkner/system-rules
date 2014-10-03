package org.junit.contrib.java.lang.system;

import org.junit.Test;
import org.junit.runners.model.Statement;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.lang.System.out;
import static java.lang.System.setOut;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class StandardOutputStreamLogTest {
	private static final String ARBITRARY_TEXT = "arbitrary text";

	private final StandardOutputStreamLog log = new StandardOutputStreamLog();
	private final StandardOutputStreamLog silentLog = new StandardOutputStreamLog(true);

	@Test
	public void logWriting() throws Throwable {
		executeRuleWithStatement(new WriteTextToStandardOutputStream());
		assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
	}

	@Test
	public void restoreSystemOutputStream() throws Throwable {
		PrintStream originalStream = out;
		executeRuleWithStatement(new WriteTextToStandardOutputStream());
		assertThat(originalStream, is(sameInstance(out)));
	}

	@Test
	public void stillWritesToSystemOutputStream() throws Throwable {
		PrintStream originalStream = out;
		try {
			ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
			executeRuleWithStatement(new WriteTextToStandardOutputStream());
			assertThat(captureOutputStream,
					hasToString(equalTo(ARBITRARY_TEXT)));
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void collectsLogAfterClearing() throws Throwable {
		executeRuleWithStatement(new ClearLogWhileWritingTextToStandardOutputStream());
		assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
	}

    @Test
    public void silentDoesNotDuplicate() throws Throwable {
        PrintStream originalStream = out;
        try {
            ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
            setOut(new PrintStream(captureOutputStream));
            silentExecuteRuleWithStatement(new WriteTextToStandardOutputStream());
            assertThat(captureOutputStream.size(), is(0));
        } finally {
            setOut(originalStream);
        }
    }

	private void executeRuleWithStatement(Statement statement) throws Throwable {
		log.apply(statement, null).evaluate();
	}

	private void silentExecuteRuleWithStatement(Statement statement) throws Throwable {
		silentLog.apply(statement, null).evaluate();
	}

	private class WriteTextToStandardOutputStream extends Statement {
		@Override
		public void evaluate() throws Throwable {
			out.print(ARBITRARY_TEXT);
		}
	}

	private class ClearLogWhileWritingTextToStandardOutputStream extends
			Statement {
		@Override
		public void evaluate() throws Throwable {
			out.print(ARBITRARY_TEXT);
			log.clear();
			out.print(ARBITRARY_TEXT);
		}
	}
}
