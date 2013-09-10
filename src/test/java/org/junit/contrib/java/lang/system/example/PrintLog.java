package org.junit.contrib.java.lang.system.example;

public class PrintLog {
	public void doPrint(String s) {
		System.out.print(s);
		System.err.print(s);
	}
}