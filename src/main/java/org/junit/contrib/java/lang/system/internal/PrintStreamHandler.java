package org.junit.contrib.java.lang.system.internal;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.System.setErr;
import static java.lang.System.setOut;

import java.io.PrintStream;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public enum PrintStreamHandler {
	SYSTEM_OUT {
		@Override
		PrintStream getStream() {
			return out;
		}

		@Override
		void replaceCurrentStreamWithStream(PrintStream stream) {
			setOut(stream);
		}
	},
	SYSTEM_ERR {
		@Override
		PrintStream getStream() {
			return err;
		}

		@Override
		void replaceCurrentStreamWithStream(PrintStream stream) {
			setErr(stream);
		}
	};

	Statement createRestoreStatement(final Statement base) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				PrintStream originalStream = getStream();
				try {
					base.evaluate();
				} finally {
					replaceCurrentStreamWithStream(originalStream);
				}
			}
		};
	}

	abstract PrintStream getStream();

	abstract void replaceCurrentStreamWithStream(PrintStream stream);
}
