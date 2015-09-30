package org.junit.contrib.java.lang.system;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.lang.System.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;
import static org.junit.contrib.java.lang.system.Statements.writeTextToSystemOut;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;
import org.junit.runners.model.Statement;

public class StandardOutputStreamLogTest {
	@Test
	public void log_contains_text_that_has_been_written_to_system_out() {
		StandardOutputStreamLog rule = createLogWithoutSpecificMode();
		executeTestWithRule(writeTextToSystemOut("dummy text"), rule);
		assertThat(rule.getLog()).isEqualTo("dummy text");
	}

	@Test
	public void after_the_test_system_out_is_same_as_before() {
		StandardOutputStreamLog rule = createLogWithoutSpecificMode();
		PrintStream originalStream = out;
		executeTestWithRule(writeTextToSystemOut("dummy text"), rule);
		assertThat(originalStream).isSameAs(out);
	}

	@Test
	public void text_is_still_written_to_system_out_if_no_log_mode_is_specified() {
		PrintStream originalStream = out;
		try {
			ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
			StandardOutputStreamLog rule = createLogWithoutSpecificMode();
			executeTestWithRule(writeTextToSystemOut("dummy text"), rule);
			assertThat(captureOutputStream.toString()).isEqualTo("dummy text");
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void no_text_is_written_to_system_out_if_log_mode_is_log_only() {
		PrintStream originalStream = out;
		try {
			ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
			StandardOutputStreamLog rule = new StandardOutputStreamLog(LogMode.LOG_ONLY);
			executeTestWithRule(writeTextToSystemOut("dummy text"), rule);
			assertThat(captureOutputStream.toString()).isEmpty();
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void log_contains_only_text_that_has_been_written_after_log_was_cleared() {
		StandardOutputStreamLog rule = createLogWithoutSpecificMode();
		executeTestWithRule(new ClearLogDuringTest(rule), rule);
		assertThat(rule.getLog()).isEqualTo(ClearLogDuringTest.TEXT);
	}

	@Test
	public void rule_cannot_be_created_without_log_mode() {
		Throwable exception = exceptionThrownBy(
			new com.github.stefanbirkner.fishbowl.Statement() {
				public void evaluate() throws Throwable {
					new StandardOutputStreamLog(null);
				}
			});
		assertThat(exception)
			.isInstanceOf(NullPointerException.class)
			.hasMessage("The LogMode is missing.");
	}

	private StandardOutputStreamLog createLogWithoutSpecificMode() {
		return new StandardOutputStreamLog();
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
