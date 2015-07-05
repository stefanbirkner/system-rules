package org.junit.contrib.java.lang.system;

import static java.lang.String.format;
import static java.lang.System.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
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
	public void after_the_test_system_out_is_same_as_before() throws Throwable {
		SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, new Statement() {
			@Override
			public void evaluate() throws Throwable {
				PrintStream otherOut = new PrintStream(
					new ByteArrayOutputStream());
				setOut(otherOut);
			}
		});
		assertThat(out, is(sameInstance(originalOut)));
	}

	@Test
	public void text_is_still_written_to_system_out_by_default()
		throws Throwable {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(systemOut, hasToString(equalTo("arbitrary text")));
	}

	@Test
	public void no_text_is_written_to_system_out_if_muted_globally()
		throws Throwable {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule().mute();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(systemOut, hasToString(isEmptyString()));
	}

	@Test
	public void no_text_is_written_to_system_out_after_muted_locally()
		throws Throwable {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		final SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, new Statement() {
			@Override
			public void evaluate() throws Throwable {
				out.print("text before muting");
				rule.mute();
				out.print("text after muting");
			}
		});
		assertThat(systemOut, hasToString("text before muting"));
	}

	@Test
	public void no_text_is_written_to_system_out_for_successful_test_if_muted_globally_for_successful_tests()
		throws Throwable{
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule().muteForSuccessfulTests();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(systemOut, hasToString(isEmptyString()));
	}

	@Test
	public void text_is_written_to_system_out_for_failing_test_if_muted_globally_for_successful_tests()
		throws Throwable {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule().muteForSuccessfulTests();
		executeRuleWithStatement(rule, new Statement() {
			@Override
			public void evaluate() throws Throwable {
				out.print("arbitrary text");
				fail();
			}
		});
		assertThat(systemOut, hasToString("arbitrary text"));
	}

	@Test
	public void no_text_is_written_to_system_out_for_sucessful_test_if_muted_locally_for_successful_tests()
		throws Throwable{
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		final SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, new Statement() {
			@Override
			public void evaluate() throws Throwable {
				rule.muteForSuccessfulTests();
				out.print("arbitrary text");
			}
		});
		assertThat(systemOut, hasToString(isEmptyString()));
	}

	@Test
	public void text_is_written_to_system_out_for_failing_test_if_muted_locally_for_successful_tests()
		throws Throwable{
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		final SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, new Statement() {
			@Override
			public void evaluate() throws Throwable {
				rule.muteForSuccessfulTests();
				out.print("arbitrary text");
				fail();
			}
		});
		assertThat(systemOut, hasToString("arbitrary text"));
	}

	@Test
	public void no_text_is_logged_by_default() throws Throwable {
		SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(rule.getLog(), isEmptyString());
	}

	@Test
	public void text_is_logged_if_log_has_been_enabled_globally() throws Throwable {
		SystemOutRule rule = new SystemOutRule().enableLog();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(rule.getLog(), is(equalTo("arbitrary text")));
	}

	@Test
	public void text_is_logged_after_log_has_been_enabled_locally() throws Throwable {
		final SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, new Statement() {
			@Override
			public void evaluate() throws Throwable {
				out.print("text before enabling log");
				rule.enableLog();
				out.print("arbitrary text");
			}
		});
		assertThat(rule.getLog(), is(equalTo("arbitrary text")));
	}

	@Test
	public void log_contains_only_text_that_has_been_written_after_log_was_cleared()
			throws Throwable {
		final SystemOutRule rule = new SystemOutRule().enableLog();
		executeRuleWithStatement(rule, new Statement() {
			@Override
			public void evaluate() throws Throwable {
				out.print("text that is cleared");
				rule.clearLog();
				out.print("arbitrary text");
			}
		});
		assertThat(rule.getLog(), is(equalTo("arbitrary text")));
	}

	@Test
	public void text_is_logged_if_rule_is_enabled_and_muted() throws Throwable {
		SystemOutRule rule = new SystemOutRule().enableLog().mute();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(rule.getLog(), is(equalTo("arbitrary text")));
	}

	@Test
	public void log_is_provided_with_new_line_characters_only_if_requested()
		throws Throwable {
		setProperty("line.separator", "\r\n");
		SystemOutRule rule = new SystemOutRule().enableLog();
		executeRuleWithStatement(rule, writeTextToSystemOut(format("arbitrary%ntext%n")));
		assertThat(rule.getLogWithNormalizedLineSeparator(), is(equalTo("arbitrary\ntext\n")));
	}

	private ByteArrayOutputStream useReadableSystemOut() {
		ByteArrayOutputStream readableStream = new ByteArrayOutputStream();
		setOut(new PrintStream(readableStream));
		return readableStream;
	}

	private void executeRuleWithStatement(TestRule rule, Statement statement)
		throws Throwable {
		try {
			rule.apply(statement, null).evaluate();
		} catch (AssertionError ignored) {
			//we must ignore the exception in case of a failing statement.
		}
	}
}
