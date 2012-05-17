package org.junit.contrib.java.lang.system.example;

import static org.junit.Assert.assertEquals;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;
import static org.junit.contrib.java.lang.system.example.Summarize.sumOfNumbersFromSystemIn;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

public class SummarizeTest {
	@Rule
	public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

	@Test
	public void summarizesTwoNumbers() {
		systemInMock.provideText("1\n2\n");
		assertEquals(3, sumOfNumbersFromSystemIn());
	}
}
