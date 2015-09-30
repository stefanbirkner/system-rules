package org.junit.contrib.java.lang.system;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.lang.System.err;
import static java.lang.System.setErr;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;
import static org.junit.contrib.java.lang.system.Statements.writeTextToSystemErr;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.junit.runners.model.Statement;

public class StandardErrorStreamLogTest {
	@Test
	public void log_contains_text_that_has_been_written_to_system_err()
			throws Throwable {
		StandardErrorStreamLog rule = createLogWithoutSpecificMode();
		executeTestWithRule(writeTextToSystemErr("dummy text"), rule);
		assertThat(rule.getLog()).isEqualTo("dummy text");
	}

	@Test
	public void after_the_test_system_err_is_same_as_before() {
		StandardErrorStreamLog rule = createLogWithoutSpecificMode();
		PrintStream originalStream = err;
		executeTestWithRule(writeTextToSystemErr("dummy text"), rule);
		assertThat(originalStream).isSameAs(err);
	}

	@Test
	public void text_is_still_written_to_system_err_if_no_log_mode_is_specified() {
		PrintStream originalStream = err;
		try {
			ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
			StandardErrorStreamLog rule = createLogWithoutSpecificMode();
			executeTestWithRule(writeTextToSystemErr("dummy text"), rule);
			assertThat(captureErrorStream.toString()).isEqualTo("dummy text");
		} finally {
			setErr(originalStream);
		}
	}

	@Test
	public void no_text_is_written_to_system_err_if_log_mode_is_log_only() {
		PrintStream originalStream = err;
		try {
			ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
			StandardErrorStreamLog rule = new StandardErrorStreamLog(LogMode.LOG_ONLY);
			executeTestWithRule(writeTextToSystemErr("dummy text"), rule);
			assertThat(captureErrorStream.toString()).isEmpty();
		} finally {
			setErr(originalStream);
		}
	}

	@Test
	public void log_contains_only_text_that_has_been_written_after_log_was_cleared() {
		StandardErrorStreamLog rule = createLogWithoutSpecificMode();
		executeTestWithRule(new ClearLogDuringTest(rule), rule);
		assertThat(rule.getLog()).isEqualTo(ClearLogDuringTest.TEXT);
	}

	@Test
	public void rule_cannot_be_created_without_log_mode() {
		Throwable exception = exceptionThrownBy(
			new com.github.stefanbirkner.fishbowl.Statement() {
				public void evaluate() throws Throwable {
					new StandardErrorStreamLog(null);
				}
			});
		assertThat(exception)
			.isInstanceOf(NullPointerException.class)
			.hasMessage("The LogMode is missing.");
	}

	private StandardErrorStreamLog createLogWithoutSpecificMode() {
		return new StandardErrorStreamLog();
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
