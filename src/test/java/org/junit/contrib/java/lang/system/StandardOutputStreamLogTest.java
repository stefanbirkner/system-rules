package org.junit.contrib.java.lang.system;

import static java.lang.System.out;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.PrintStream;

import org.junit.Test;
import org.junit.runners.model.Statement;

public class StandardOutputStreamLogTest {
	private static final String ARBITRARY_TEXT = "arbitrary text";

	private final StandardOutputStreamLog log = new StandardOutputStreamLog();

	@Test
	public void logWriting() throws Throwable {
		executeRuleWithStatement();
		assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
	}

	@Test
	public void restoreSystemOutputStream() throws Throwable {
		PrintStream originalStream = out;
		executeRuleWithStatement();
		assertThat(originalStream, is(sameInstance(out)));
	}

	private void executeRuleWithStatement() throws Throwable {
		log.apply(new WriteTextToStandardOutputStream(), null).evaluate();
	}

	private class WriteTextToStandardOutputStream extends Statement {
		@Override
		public void evaluate() throws Throwable {
			out.print(ARBITRARY_TEXT);
		}
	}
}
