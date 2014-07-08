package org.junit.contrib.java.lang.system.internal;

import static java.lang.System.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RestoreSpecificSystemProperties {
	private final List<String> properties = new ArrayList<String>();
	private final List<String> originalValues = new ArrayList<String>();

	public void add(String property) {
		properties.add(property);
		originalValues.add(getProperty(property));
	}

	public void restore() {
		Iterator<String> itOriginalValues = originalValues.iterator();
		for (String property : properties)
			restore(property, itOriginalValues.next());
	}

	private void restore(String property, String originalValue) {
		if (originalValue == null)
			clearProperty(property);
		else
			setProperty(property, originalValue);
	}
}
