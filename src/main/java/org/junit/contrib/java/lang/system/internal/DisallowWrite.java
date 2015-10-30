package org.junit.contrib.java.lang.system.internal;

import org.junit.runners.model.Statement;

import java.io.IOException;
import java.io.OutputStream;

public class DisallowWrite {
	private final PrintStreamHandler printStreamHandler;

	public DisallowWrite(PrintStreamHandler printStreamHandler) {
		this.printStreamHandler = printStreamHandler;
	}

	public Statement createStatement(final Statement base) {
		return printStreamHandler.createRestoreStatement(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				printStreamHandler.replaceCurrentStreamWithOutputStream(
					new DisallowWriteStream());
				base.evaluate();
			}
		});
	}

	private static class DisallowWriteStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			throw new AssertionError("Tried to write '" + (char) b
				+ "' although this is not allowed.");
		}
	}
}
