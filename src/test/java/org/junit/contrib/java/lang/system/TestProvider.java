package org.junit.contrib.java.lang.system;

import java.security.Provider;

public class TestProvider extends Provider {
	protected TestProvider() {
		super("test", 0.1, "test");
	}
}
