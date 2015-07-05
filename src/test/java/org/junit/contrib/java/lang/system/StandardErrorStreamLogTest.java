package org.junit.contrib.java.lang.system;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.lang.System.err;
import static java.lang.System.setErr;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.contrib.java.lang.system.Statements.writeTextToSystemErr;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

public class StandardErrorStreamLogTest {
	@Test
	public void log_contains_text_that_has_been_written_to_system_err()
			throws Throwable {
		StandardErrorStreamLog rule = createLogWithoutSpecificMode();
		executeRuleWithStatement(rule, writeTextToSystemErr("dummy text"));
		assertThat(rule.getLog(), is(equalTo("dummy text")));
	}

	@Test
	public void after_the_test_system_err_is_same_as_before() throws Throwable {
		StandardErrorStreamLog rule = createLogWithoutSpecificMode();
		PrintStream originalStream = err;
		executeRuleWithStatement(rule, writeTextToSystemErr("dummy text"));
		assertThat(originalStream, is(sameInstance(err)));
	}

	@Test
	public void text_is_still_written_to_system_err_if_no_log_mode_is_specified()
			throws Throwable {
		PrintStream originalStream = err;
		try {
			ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
			StandardErrorStreamLog rule = createLogWithoutSpecificMode();
			executeRuleWithStatement(rule, writeTextToSystemErr("dummy text"));
			assertThat(captureErrorStream, hasToString(equalTo("dummy text")));
		} finally {
			setErr(originalStream);
		}
	}

	@Test
	public void no_text_is_written_to_system_err_if_log_mode_is_log_only()
			throws Throwable {
		PrintStream originalStream = err;
		try {
			ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
			StandardErrorStreamLog rule = new StandardErrorStreamLog(LogMode.LOG_ONLY);
			executeRuleWithStatement(rule, writeTextToSystemErr("dummy text"));
			assertThat(captureErrorStream, hasToString(isEmptyString()));
		} finally {
			setErr(originalStream);
		}
	}

	@Test
	public void log_contains_only_text_that_has_been_written_after_log_was_cleared()
			throws Throwable {
		StandardErrorStreamLog log = createLogWithoutSpecificMode();
		executeRuleWithStatement(log, new ClearLogDuringTest(log));
		assertThat(log.getLog(), is(equalTo(ClearLogDuringTest.TEXT)));
	}

	@Test
	public void rule_cannot_be_created_without_log_mode() {
		Throwable exception = exceptionThrownBy(
			new com.github.stefanbirkner.fishbowl.Statement() {
				public void evaluate() throws Throwable {
					new StandardErrorStreamLog(null);
				}
			});
		assertThat(exception, allOf(
			instanceOf(NullPointerException.class),
			hasProperty("message", equalTo("The LogMode is missing."))));
	}

	private StandardErrorStreamLog createLogWithoutSpecificMode() {
		return new StandardErrorStreamLog();
	}

	private void executeRuleWithStatement(TestRule rule, Statement statement) throws Throwable {
		rule.apply(statement, null).evaluate();
	}

	private class ClearLogDuringTest extends Statement {
		private static final String TEXT = "arbitrary text";
		private final StandardErrorStreamLog log;

		private ClearLogDuringTest(StandardErrorStreamLog log) {
			this.log = log;
		}

		@Override
		public void evaluate() throws Throwable {
			err.print(TEXT);
			log.clear();
			err.print(TEXT);
		}
	}
}
