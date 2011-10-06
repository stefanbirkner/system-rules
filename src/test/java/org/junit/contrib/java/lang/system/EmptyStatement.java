package org.junit.contrib.java.lang.system;

import org.junit.runners.model.Statement;

class EmptyStatement extends Statement {
	@Override
	public void evaluate() throws Throwable {
	}
}