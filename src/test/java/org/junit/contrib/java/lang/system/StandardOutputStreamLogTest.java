package org.junit.contrib.java.lang.system;

import static java.lang.System.out;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.Statement;

@RunWith(value = Parameterized.class)
public class StandardOutputStreamLogTest {
	private static final String ARBITRARY_TEXT = "arbitrary text";

	private final StandardOutputStreamLog log;

	@Parameters
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] { { null }, { Boolean.TRUE }, { Boolean.FALSE } };
		return Arrays.asList(data);
	}

	public StandardOutputStreamLogTest(Boolean keepOutput) {
		if (keepOutput == null) {
			log = new StandardOutputStreamLog();
		}
		else {
			log = new StandardOutputStreamLog(keepOutput);
		}
	}

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
