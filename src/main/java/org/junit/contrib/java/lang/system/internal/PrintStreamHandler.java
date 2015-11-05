package org.junit.contrib.java.lang.system.internal;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.System.setErr;
import static java.lang.System.setOut;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.runners.model.Statement;

public enum PrintStreamHandler {
	SYSTEM_OUT {
		@Override
		PrintStream getStream() {
			return out;
		}

		@Override
		void replaceCurrentStreamWithPrintStream(PrintStream stream) {
			setOut(stream);
		}
	},
	SYSTEM_ERR {
		@Override
		PrintStream getStream() {
			return err;
		}

		@Override
		void replaceCurrentStreamWithPrintStream(PrintStream stream) {
			setErr(stream);
		}
	};

	private static final boolean AUTO_FLUSH = true;
	private static final String DEFAULT_ENCODING = Charset.defaultCharset().name();

	Statement createRestoreStatement(final Statement base) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				PrintStream originalStream = getStream();
				try {
					base.evaluate();
				} finally {
					replaceCurrentStreamWithPrintStream(originalStream);
				}
			}
		};
	}

	void replaceCurrentStreamWithOutputStream(OutputStream outputStream)
			throws UnsupportedEncodingException {
		PrintStream printStream = new PrintStream(
			outputStream, AUTO_FLUSH, DEFAULT_ENCODING);
		replaceCurrentStreamWithPrintStream(printStream);
	}

	abstract PrintStream getStream();

	abstract void replaceCurrentStreamWithPrintStream(PrintStream stream);
}
