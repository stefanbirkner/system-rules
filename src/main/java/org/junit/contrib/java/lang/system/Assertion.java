package org.junit.contrib.java.lang.system;

/**
 * An {@code Assertion} encapsulates the code of an assertion into an object.
 */
public interface Assertion {
	void checkAssertion() throws Exception;
}
