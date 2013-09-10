package org.junit.contrib.java.lang.system.example;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardErrorStreamLog;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;

public class PrintLogTest {
	
	PrintLog app = new PrintLog();
	String s = "some test";
	
	/**
	 * logging to sysout will not print in the console anymore:
	 */
	@Rule
	public StandardOutputStreamLog out = new StandardOutputStreamLog(false);
	
	/**
	 * logging to syserr will still be shown in the console:
	 */
	@Rule
	public StandardErrorStreamLog err = new StandardErrorStreamLog(true);
	
	@Test
	public void testDoPrint() {
		app.doPrint(s);
		Assert.assertEquals(s, out.getLog());
		Assert.assertEquals(s, err.getLog());
	}
}
