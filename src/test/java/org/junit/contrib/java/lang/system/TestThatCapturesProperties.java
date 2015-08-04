package org.junit.contrib.java.lang.system;

import org.junit.runners.model.Statement;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.getProperties;

class TestThatCapturesProperties extends Statement {
	Map<Object, Object> propertiesAtStart;

	@Override
	public void evaluate() throws Throwable {
		propertiesAtStart = new HashMap<Object, Object>();
		Enumeration names = getProperties().propertyNames();
		while (names.hasMoreElements())
			storeProperty((String) names.nextElement());
	}

	private void storeProperty(String key) {
		Object value = getProperties().getProperty(key);
		propertiesAtStart.put(key, value);
	}
}
