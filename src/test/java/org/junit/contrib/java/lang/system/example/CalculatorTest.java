package org.junit.contrib.java.lang.system.example;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

public class CalculatorTest {
	private static final String NEW_LINE = System.getProperty("line.separator");

	@Rule
	public final TextFromStandardInputStream systemInMock
		= emptyStandardInputStream();

	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().mute().enableLog();

	@Rule
	public final SystemErrRule systemErrRule = new SystemErrRule().mute().enableLog();

	@Test
	public void calculatesSingleSum() throws Exception {
		systemInMock.provideText("1+2" + NEW_LINE + "quit" + NEW_LINE);
		runCalculator();
		String result = systemOutRule.getLog();
		assertEquals("3" + NEW_LINE, result);
	}

	@Test
	public void calculatesMultipleSums() throws Exception {
		systemInMock.provideLines("1+2", "3+4", "quit");
		runCalculator();
		String result = systemOutRule.getLog();
		assertEquals("3" + NEW_LINE + "7" + NEW_LINE, result);
	}

	@Test
	public void printsErrorMessageForInvalidInput() throws Exception {
		systemInMock.provideLines("1+a", "quit");
		runCalculator();
		String result = systemErrRule.getLog();
		assertEquals("Invalid input: 1+a" + NEW_LINE, result);
	}

	private void waitAMoment() throws InterruptedException {
		Thread.sleep(100);
	}

	private void startCalculator() {
		new Thread(new Runnable() {
			public void run() {
				runCalculator();
			}
		}).start();
	}

	private void runCalculator() {
		Calculator.main();
	}
}
