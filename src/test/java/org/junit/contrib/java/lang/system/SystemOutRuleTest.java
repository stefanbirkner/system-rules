package org.junit.contrib.java.lang.system;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.lang.System.setOut;
import static java.lang.System.setProperty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
	public void restoreSystemOut() {
		setOut(originalOut);
	}

	@Test
	public void restoresSystemOut() throws Throwable {
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
	public void doesNotMuteSystemOutByDefault() throws Throwable {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(systemOut, hasToString(equalTo("arbitrary text")));
	}

	@Test
	public void doesNotWriteToSystemOutIfMutedGlobally() throws Throwable {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule().mute();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(systemOut, hasToString(isEmptyString()));
	}

	@Test
	public void doesNotWriteToSystemOutIfMutedLocally() throws Throwable {
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		final SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, new Statement() {
			@Override
			public void evaluate() throws Throwable {
				rule.mute();
				out.print("arbitrary text");
			}
		});
		assertThat(systemOut, hasToString(isEmptyString()));
	}

	@Test
	public void doesNotWriteToSystemOutForSuccessfulTestIfMutedGloballyForSuccessfulTests()
			throws Throwable{
		ByteArrayOutputStream systemOut = useReadableSystemOut();
		SystemOutRule rule = new SystemOutRule().muteForSuccessfulTests();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(systemOut, hasToString(isEmptyString()));
	}

	@Test
	public void writesToSystemOutForFailingTestIfMutedGloballyForSuccessfulTests()
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
	public void doesNotWriteToSystemOutForSuccessfulTestIfMutedLocallyForSuccessfulTests()
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
	public void writesToSystemOutForFailingTestIfMutedLocallyForSuccessfulTests()
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
	public void doesNotLogByDefault() throws Throwable {
		SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(rule.getLog(), isEmptyString());
	}

	@Test
	public void logsIfEnabledGlobally() throws Throwable {
		SystemOutRule rule = new SystemOutRule().enableLog();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(rule.getLog(), is(equalTo("arbitrary text")));
	}

	@Test
	public void logsIfEnabledLocally() throws Throwable {
		final SystemOutRule rule = new SystemOutRule();
		executeRuleWithStatement(rule, new Statement() {
			@Override
			public void evaluate() throws Throwable {
				rule.enableLog();
				out.print("arbitrary text");
			}
		});
		assertThat(rule.getLog(), is(equalTo("arbitrary text")));
	}

	@Test
	public void collectsLogAfterClearing() throws Throwable {
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
	public void logsIfMuted() throws Throwable {
		SystemOutRule rule = new SystemOutRule().enableLog().mute();
		executeRuleWithStatement(rule, writeTextToSystemOut("arbitrary text"));
		assertThat(rule.getLog(), is(equalTo("arbitrary text")));
	}

	@Test
	public void providesLogWithNormalizedNewLineCharacters() throws Throwable {
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

	private Statement writeTextToSystemOut(final String text) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				out.print(text);
			}
		};
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
