package org.junit.contrib.java.lang.system.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.TeeOutputStream;
import org.junit.rules.ExternalResource;

public abstract class PrintStreamLog extends ExternalResource {
	private static final boolean NO_AUTO_FLUSH = false;
	private static final String ENCODING = "UTF-8";
	private final ByteArrayOutputStream log = new ByteArrayOutputStream();
	private PrintStream originalStream;
	protected final boolean keepOutput;
	
	/**
	 * Default: will redirect to {@link #log}
	 */
	public PrintStreamLog() {
		keepOutput = false;
	}

	/**
	 * Allow to keep printing the output in the original Stream
	 * 
	 * @param keepOutput true to print in the readable {@link ByteArrayInputStream} and in the original stream at the same time
	 */
	public PrintStreamLog(boolean keepOutput) {
		this.keepOutput = keepOutput;
	}
	
	@Override
	protected void before() throws Throwable {
		originalStream = getOriginalStream();
		PrintStream wrappedLog = new PrintStream(log, NO_AUTO_FLUSH, ENCODING);
		if(keepOutput) {
			wrappedLog = new PrintStream(new TeeOutputStream(log, originalStream), NO_AUTO_FLUSH, ENCODING);
		}
		else {
			wrappedLog = new PrintStream(log, NO_AUTO_FLUSH, ENCODING);
		}
		setStream(wrappedLog);
	}

	@Override
	protected void after() {
		setStream(originalStream);
	}

	protected abstract PrintStream getOriginalStream();

	protected abstract void setStream(PrintStream wrappedLog);

	/**
	 * Returns the text written to the standard error stream.
	 * 
	 * @return the text written to the standard error stream.
	 */
	public String getLog() {
		try {
			return log.toString(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}