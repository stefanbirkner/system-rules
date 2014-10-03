package org.junit.contrib.java.lang.system.internal;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.junit.rules.ExternalResource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public abstract class PrintStreamLog extends ExternalResource {
	private static final boolean NO_AUTO_FLUSH = false;
	private static final String ENCODING = "UTF-8";
	private final ByteArrayOutputStream log = new ByteArrayOutputStream();
    private final boolean silent;
	private PrintStream originalStream;

    protected PrintStreamLog(final boolean silent) {
        this.silent = silent;
    }

    @Override
	protected void before() throws Throwable {
		originalStream = getOriginalStream();
		TeeOutputStream tee = new TeeOutputStream(silent ? new NullOutputStream() : originalStream, log);
		PrintStream wrappedLog = new PrintStream(tee, NO_AUTO_FLUSH, ENCODING);
		setStream(wrappedLog);
	}

	@Override
	protected void after() {
		setStream(originalStream);
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
