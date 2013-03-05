package org.junit.contrib.java.lang.system.example;

public class AppWithExit {
	public static String message;

	public static void doSomethingAndExit() {
		message = "exit ...";
		System.exit(1);
	}

	public static void doNothing() {
	}
}
