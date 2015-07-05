package org.junit.contrib.java.lang.system;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.lang.System.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.contrib.java.lang.system.Statements.writeTextToSystemOut;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

public class StandardOutputStreamLogTest {
	@Test
	public void log_contains_text_that_has_been_written_to_system_out()
		throws Throwable {
		StandardOutputStreamLog rule = createLogWithoutSpecificMode();
		executeRuleWithStatement(rule, writeTextToSystemOut("dummy text"));
		assertThat(rule.getLog(), is(equalTo("dummy text")));
	}

	@Test
	public void after_the_test_system_out_is_same_as_before() throws Throwable {
		StandardOutputStreamLog rule = createLogWithoutSpecificMode();
		PrintStream originalStream = out;
		executeRuleWithStatement(rule, writeTextToSystemOut("dummy text"));
		assertThat(originalStream, is(sameInstance(out)));
	}

	@Test
	public void text_is_still_written_to_system_out_if_no_log_mode_is_specified()
		throws Throwable {
		PrintStream originalStream = out;
		try {
			ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
			StandardOutputStreamLog rule = createLogWithoutSpecificMode();
			executeRuleWithStatement(rule, writeTextToSystemOut("dummy text"));
			assertThat(captureOutputStream, hasToString(equalTo("dummy text")));
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void no_text_is_written_to_system_out_if_log_mode_is_log_only()
		throws Throwable {
		PrintStream originalStream = out;
		try {
			ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
			StandardOutputStreamLog rule = new StandardOutputStreamLog(LogMode.LOG_ONLY);
			executeRuleWithStatement(rule, writeTextToSystemOut("dummy text"));
			assertThat(captureOutputStream, hasToString(isEmptyString()));
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void log_contains_only_text_that_has_been_written_after_log_was_cleared()
		throws Throwable {
		StandardOutputStreamLog log = createLogWithoutSpecificMode();
		executeRuleWithStatement(log, new ClearLogDuringTest(log));
		assertThat(log.getLog(), is(equalTo(ClearLogDuringTest.TEXT)));
	}

	@Test
	public void rule_cannot_be_created_without_log_mode() {
		Throwable exception = exceptionThrownBy(
			new com.github.stefanbirkner.fishbowl.Statement() {
				public void evaluate() throws Throwable {
					new StandardOutputStreamLog(null);
				}
			});
		assertThat(exception, allOf(
			instanceOf(NullPointerException.class),
			hasProperty("message", equalTo("The LogMode is missing."))));
	}

	private StandardOutputStreamLog createLogWithoutSpecificMode() {
		return new StandardOutputStreamLog();
	}

	private void executeRuleWithStatement(TestRule rule, Statement statement) throws Throwable {
		rule.apply(statement, null).evaluate();
	}

	private class ClearLogDuringTest extends Statement {
		private static final String TEXT = "arbitrary text";
		private final StandardOutputStreamLog log;

		private ClearLogDuringTest(StandardOutputStreamLog log) {
			this.log = log;
		}

		@Override
		public void evaluate() throws Throwable {
			out.print(TEXT);
			log.clear();
			out.print(TEXT);
		}
	}
}
