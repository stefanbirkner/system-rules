package org.junit.contrib.java.lang.system;

import static java.lang.System.in;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Statements.SUCCESSFUL_TEST;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

import java.io.InputStream;
import java.util.Scanner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runners.model.Statement;

public class TextFromStandardInputStreamTest {
	@Rule
	public final Timeout timeout = new Timeout(1000);

	private final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

	@Test
	public void provided_text_is_available_from_system_in() throws Throwable {
		executeRuleWithStatement(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.provideText("arbitrary text");
				Scanner scanner = new Scanner(in);
				String textFromSystemIn = scanner.nextLine();
				assertThat(textFromSystemIn).isEqualTo("arbitrary text");
			}
		});
	}

	@Test
	public void specified_texts_are_available_from_system_in()
			throws Throwable {
		executeRuleWithStatement(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.provideText("first text\n", "second text\n");
				Scanner firstScanner = new Scanner(in);
				firstScanner.nextLine();
				Scanner secondScanner = new Scanner(in);
				String textFromSystemIn = secondScanner.nextLine();
				assertThat(textFromSystemIn).isEqualTo("second text");
			}
		});
	}

	@Test
	public void no_text_is_available_from_system_in_if_no_text_has_been_provided()
			throws Throwable {
		executeRuleWithStatement(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.provideText();
				int character = in.read();
				assertThat(character).isEqualTo(-1);
			}
		});
	}

	@Test
	public void specified_lines_are_available_from_system_in() throws Throwable {
		executeRuleWithStatement(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.provideLines("first text", "second text");
				Scanner firstScanner = new Scanner(in);
				firstScanner.nextLine();
				Scanner secondScanner = new Scanner(in);
				String textFromSystemIn = secondScanner.nextLine();
				assertThat(textFromSystemIn).isEqualTo("second text");
			}
		});
	}

	@Test
	public void no_text_is_available_from_system_in_if_no_line_has_been_provided()
			throws Throwable {
		executeRuleWithStatement(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				systemInMock.provideLines();
				int character = in.read();
				assertThat(character).isEqualTo(-1);
			}
		});
	}

	@Test
	public void after_the_test_system_in_is_same_as_before() throws Throwable {
		InputStream originalSystemIn = in;
		executeRuleWithStatement(SUCCESSFUL_TEST);
		assertThat(in).isSameAs(originalSystemIn);
	}

	private void executeRuleWithStatement(Statement statement) throws Throwable {
		systemInMock.apply(statement, null).evaluate();
	}
}
