package org.junit.contrib.java.lang.system;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.rules.ExternalResource;

abstract class PrintStreamLog extends ExternalResource {
	private final ByteArrayOutputStream log = new ByteArrayOutputStream();
	private PrintStream originalStandardErrorStream;

	@Override
	protected void before() throws Throwable {
		originalStandardErrorStream = getOriginalStream();
		PrintStream wrappedLog = new PrintStream(log);
		setStream(wrappedLog);
	}

	@Override
	protected void after() {
		setStream(originalStandardErrorStream);
	}

	abstract PrintStream getOriginalStream();

	abstract void setStream(PrintStream wrappedLog);

	/**
	 * Returns the text written to the standard error stream.
	 * 
	 * @return the text written to the standard error stream.
	 */
	public String getLog() {
		try {
			return log.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}