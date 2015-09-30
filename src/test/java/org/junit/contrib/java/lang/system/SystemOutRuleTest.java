package org.junit.contrib.java.lang.system;

import static java.lang.String.format;
import static java.lang.System.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.junit.contrib.java.lang.system.Executor.executeFailingTestWithRule;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;
import static org.junit.contrib.java.lang.system.Statements.writeTextToSystemOut;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

public class SystemOutRuleTest {
	private final PrintStream originalOut = out;

	@Rule
	public TestRule restoreSystemProperties = new RestoreSystemProperties();

	@After
	public void restoreSystemErr() {
		setOut(originalOut);
	}

	@Test
	public void after_the_test_system_out_is_same_as_before() {
		SystemOutRule rule = new SystemOutRule();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				PrintStream otherOut = new PrintStream(
					new ByteArrayOutputStream());
				setOut(otherOut);
			}
		}, rule);
		assertThat(out).isSameAs(originalOut);
	}

	@Test
	public void text_is_still_written_to_system_out_by_default() {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule();
		executeTestWithRule(writeTextToSystemOut("arbitrary text"), rule);
		assertThat(systemOut.toString()).isEqualTo("arbitrary text");
	}

	@Test
	public void no_text_is_written_to_system_out_if_muted_globally() {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule().mute();
		executeTestWithRule(writeTextToSystemOut("arbitrary text"), rule);
		assertThat(systemOut.toString()).isEmpty();
	}

	@Test
	public void no_text_is_written_to_system_out_after_muted_locally() {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		final SystemOutRule rule = new SystemOutRule();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				out.print("text before muting");
				rule.mute();
				out.print("text after muting");
			}
		}, rule);
		assertThat(systemOut.toString()).isEqualTo("text before muting");
	}

	@Test
	public void no_text_is_written_to_system_out_for_successful_test_if_muted_globally_for_successful_tests() {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule().muteForSuccessfulTests();
		executeTestWithRule(writeTextToSystemOut("arbitrary text"), rule);
		assertThat(systemOut.toString()).isEmpty();
	}

	@Test
	public void text_is_written_to_system_out_for_failing_test_if_muted_globally_for_successful_tests() {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule().muteForSuccessfulTests();
		executeFailingTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				out.print("arbitrary text");
				fail();
			}
		}, rule);
		assertThat(systemOut.toString()).isEqualTo("arbitrary text");
	}

	@Test
	public void no_text_is_written_to_system_out_for_sucessful_test_if_muted_locally_for_successful_tests() {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		final SystemOutRule rule = new SystemOutRule();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				rule.muteForSuccessfulTests();
				out.print("arbitrary text");
			}
		}, rule);
		assertThat(systemOut.toString()).isEmpty();
	}

	@Test
	public void text_is_written_to_system_out_for_failing_test_if_muted_locally_for_successful_tests() {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		final SystemOutRule rule = new SystemOutRule();
		executeFailingTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				rule.muteForSuccessfulTests();
				out.print("arbitrary text");
				fail();
			}
		}, rule);
		assertThat(systemOut.toString()).isEqualTo("arbitrary text");
	}

	@Test
	public void no_text_is_logged_by_default() {
		SystemOutRule rule = new SystemOutRule();
		executeTestWithRule(writeTextToSystemOut("arbitrary text"), rule);
		assertThat(rule.getLog()).isEmpty();
	}

	@Test
	public void text_is_logged_if_log_has_been_enabled_globally() {
		SystemOutRule rule = new SystemOutRule().enableLog();
		executeTestWithRule(writeTextToSystemOut("arbitrary text"), rule);
		assertThat(rule.getLog()).isEqualTo("arbitrary text");
	}

	@Test
	public void text_is_logged_after_log_has_been_enabled_locally() {
		final SystemOutRule rule = new SystemOutRule();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				out.print("text before enabling log");
				rule.enableLog();
				out.print("arbitrary text");
			}
		}, rule);
		assertThat(rule.getLog()).isEqualTo("arbitrary text");
	}

	@Test
	public void log_contains_only_text_that_has_been_written_after_log_was_cleared() {
		final SystemOutRule rule = new SystemOutRule().enableLog();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				out.print("text that is cleared");
				rule.clearLog();
				out.print("arbitrary text");
			}
		}, rule);
		assertThat(rule.getLog()).isEqualTo("arbitrary text");
	}

	@Test
	public void text_is_logged_if_rule_is_enabled_and_muted() {
		SystemOutRule rule = new SystemOutRule().enableLog().mute();
		executeTestWithRule(writeTextToSystemOut("arbitrary text"), rule);
		assertThat(rule.getLog()).isEqualTo("arbitrary text");
	}

	@Test
	public void log_is_provided_with_new_line_characters_only_if_requested() {
		setProperty("line.separator", "\r\n");
		SystemOutRule rule = new SystemOutRule().enableLog();
		executeTestWithRule(
			writeTextToSystemOut(format("arbitrary%ntext%n")),
			rule);
		assertThat(rule.getLogWithNormalizedLineSeparator())
			.isEqualTo("arbitrary\ntext\n");
	}

	private ByteArrayOutputStream useReadableSystemOut() {
		ByteArrayOutputStream readableStream = new ByteArrayOutputStream();
		setOut(new PrintStream(readableStream));
		return readableStream;
	}
}
