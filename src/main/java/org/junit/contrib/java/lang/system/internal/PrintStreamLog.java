package org.junit.contrib.java.lang.system.internal;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.TeeOutputStream;
import org.junit.contrib.java.lang.system.LogMode;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public abstract class PrintStreamLog implements TestRule {
	private static final boolean NO_AUTO_FLUSH = false;
	private static final String ENCODING = "UTF-8";
	private final ByteArrayOutputStream log = new ByteArrayOutputStream();
	private final LogMode mode;
	private final PrintStreamHandler printStreamHandler;

	protected PrintStreamLog(LogMode mode, PrintStreamHandler printStreamHandler) {
		this.printStreamHandler = printStreamHandler;
		if (mode == null)
			throw new NullPointerException("The LogMode is missing.");
		this.mode = mode;
	}

	public Statement apply(final Statement base, Description description) {
		return printStreamHandler.createRestoreStatement(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				PrintStream wrappedStream = new PrintStream(getNewStream(), NO_AUTO_FLUSH,
					ENCODING);
				printStreamHandler.replaceCurrentStreamWithStream(wrappedStream);
				base.evaluate();
			}
		});
	}

	private OutputStream getNewStream() throws UnsupportedEncodingException {
		switch (mode) {
			case LOG_AND_WRITE_TO_STREAM:
				return new TeeOutputStream(printStreamHandler.getStream(), log);
			case LOG_ONLY:
				return log;
			default:
				throw new IllegalArgumentException("The LogMode " + mode
					+ " is not supported");
		}
	}

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
