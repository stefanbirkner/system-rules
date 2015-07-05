package org.junit.contrib.java.lang.system;

import org.junit.runners.model.Statement;

class Statements {
	static final Statement TEST_THAT_DOES_NOTHING = new Statement() {
		public void evaluate() {
		}
	};

	static final Statement SUCCESSFUL_TEST = TEST_THAT_DOES_NOTHING;

	static Statement clearProperty(final String key) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.clearProperty(key);
			}
		};
	}

	static Statement setProperty(final String key, final String value) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.setProperty(key, value);
			}
		};
	}

	static Statement writeTextToSystemErr(final String text) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.err.print(text);
			}
		};
	}

	static Statement writeTextToSystemOut(final String text) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				System.out.print(text);
			}
		};
	}
}
