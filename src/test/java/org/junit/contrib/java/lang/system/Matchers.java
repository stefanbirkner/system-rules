package org.junit.contrib.java.lang.system;

import static org.hamcrest.Matchers.*;

import org.hamcrest.Matcher;

import java.util.Map;

class Matchers {
	static Matcher<Map<?, ?>> hasPropertyWithValue(String name, String value) {
		return org.hamcrest.Matchers.<Object, Object>hasEntry(name, value);
	}

	static Matcher<Map<?, ?>> notHasProperty(String name) {
		return not(org.hamcrest.Matchers.<Object>hasKey(name));
	}
}
