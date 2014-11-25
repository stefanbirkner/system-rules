package org.junit.contrib.java.lang.system.internal;

import org.apache.commons.io.output.TeeOutputStream;
import org.junit.contrib.java.lang.system.LogMode;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public abstract class PrintStreamLog extends TestWatcher {
	private static final boolean NO_AUTO_FLUSH = false;
	private static final String ENCODING = "UTF-8";
	private final ByteArrayOutputStream log = new ByteArrayOutputStream();
	private final LogMode mode;
	private PrintStream originalStream;

	protected PrintStreamLog(LogMode mode) {
		if (mode == null)
			throw new NullPointerException("The LogMode is missing.");
		this.mode = mode;
	}

	@Override
	protected void starting(Description description) {
		try {
			originalStream = getOriginalStream();
			PrintStream wrappedStream = new PrintStream(getNewStream(),
					NO_AUTO_FLUSH, ENCODING);
			setStream(wrappedStream);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e); // JRE missing UTF-8
		}
	}

	@Override
	protected void failed(Throwable e, Description description) {
		// Only log-on-failure mode needs to print; the others already handle this
		if (mode == LogMode.LOG_AND_WRITE_TO_STREAM_ON_FAILURE_ONLY)
			originalStream.print(getLog());
	}

	@Override
	protected void finished(Description description) {
		setStream(originalStream);
	}

	private OutputStream getNewStream() {
		switch (mode) {
			case LOG_AND_WRITE_TO_STREAM:
				return new TeeOutputStream(originalStream, log);
			case LOG_ONLY:
			case LOG_AND_WRITE_TO_STREAM_ON_FAILURE_ONLY:
				return log;
			default:
				throw new IllegalArgumentException("The LogMode " + mode
					+ " is not supported");
		}
	}

	protected abstract PrintStream getOriginalStream();

	protected abstract void setStream(PrintStream wrappedLog);

	/**
	 * Clears the log. The log can be used again.
	 */
	public void clear() {
		log.reset();
	}

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
