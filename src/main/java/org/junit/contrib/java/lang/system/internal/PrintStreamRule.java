package org.junit.contrib.java.lang.system.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.TeeOutputStream;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static java.lang.System.getProperty;
import static org.apache.commons.io.IOUtils.write;

public class PrintStreamRule implements TestRule {
	private final PrintStreamHandler printStreamHandler;
	private final MuteableLogStream muteableLogStream;

	public PrintStreamRule(PrintStreamHandler printStreamHandler) {
		this.printStreamHandler = printStreamHandler;
		try {
			this.muteableLogStream = new MuteableLogStream(printStreamHandler.getStream());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					printStreamHandler.createRestoreStatement(new Statement() {
						@Override
						public void evaluate() throws Throwable {
							printStreamHandler.replaceCurrentStreamWithStream(muteableLogStream);
							base.evaluate();
						}
					}).evaluate();
				} catch (Throwable e) {
					write(muteableLogStream.getFailureLog(), printStreamHandler.getStream());
					throw e;
				}
			}
		};
	}

	public void clearLog() {
		muteableLogStream.clearLog();
	}

	public void enableLog() {
		muteableLogStream.enableLog();
	}

	public String getLog() {
		return muteableLogStream.getLog();
	}

	public String getLogWithNormalizedLineSeparator() {
		String lineSeparator = getProperty("line.separator");
		return getLog().replace(lineSeparator, "\n");
	}

	public void mute() {
		muteableLogStream.mute();
	}

	public void muteForSuccessfulTests() {
		mute();
		muteableLogStream.enableFailureLog();
	}

	private static class MuteableLogStream extends PrintStream {
		private final ByteArrayOutputStream failureLog;
		private final ByteArrayOutputStream log;
		private final MutableOutputStream muteableOriginalStream;
		private final MutableOutputStream muteableFailureLog;
		private final MutableOutputStream muteableLog;

		MuteableLogStream(OutputStream out) throws UnsupportedEncodingException {
			this(out, new ByteArrayOutputStream(), new ByteArrayOutputStream());
		}

		MuteableLogStream(OutputStream out, ByteArrayOutputStream failureLog,
				ByteArrayOutputStream log) throws UnsupportedEncodingException {
			this(new MutableOutputStream(out),
				failureLog, new MutableOutputStream(failureLog),
				log, new MutableOutputStream(log));
		}

		MuteableLogStream(MutableOutputStream muteableOriginalStream,
				ByteArrayOutputStream failureLog, MutableOutputStream muteableFailureLog,
				ByteArrayOutputStream log, MutableOutputStream muteableLog)
				throws UnsupportedEncodingException {
			super(new TeeOutputStream(
					muteableOriginalStream,
					new TeeOutputStream(muteableFailureLog, muteableLog)));
			this.failureLog = failureLog;
			this.log = log;
			this.muteableOriginalStream = muteableOriginalStream;
			this.muteableFailureLog = muteableFailureLog;
			this.muteableFailureLog.mute();
			this.muteableLog = muteableLog;
			this.muteableLog.mute();
		}

		void mute() {
			muteableOriginalStream.mute();
		}

		void clearLog() {
			log.reset();
		}

		void enableLog() {
			muteableLog.turnOutputOn();
		}

		String getLog() {
			return getLog(log);
		}

		void enableFailureLog() {
			muteableFailureLog.turnOutputOn();
		}

		String getFailureLog() {
			return getLog(failureLog);
		}

		String getLog(ByteArrayOutputStream os) {
			/* The MuteableLogStream is created with the default encoding
			 * because it writes to System.out or System.err if not muted and
			 * System.out/System.err uses the default encoding. As a result all
			 * other streams receive input that is encoded with the default
			 * encoding.
			 */
			String encoding = getProperty("file.encoding");
			try {
				return os.toString(encoding);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static class MutableOutputStream extends OutputStream {
		private final OutputStream originalStream;
		private boolean mute = false;

		MutableOutputStream(OutputStream originalStream) {
			this.originalStream = originalStream;
		}

		void mute() {
			mute = true;
		}

		void turnOutputOn() {
			mute = false;
		}

		@Override
		public void write(int b) throws IOException {
			if (!mute)
				originalStream.write(b);
		}
	}
}
