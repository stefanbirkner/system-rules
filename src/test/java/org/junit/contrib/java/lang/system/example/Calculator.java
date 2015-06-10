package org.junit.contrib.java.lang.system.example;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * An example application for using System Rules.
 */
public class Calculator {
	public static void main(String... args) {
		new Calculator().start();
	}

	private void start() {
		Scanner scanner = new Scanner(System.in);
		while (handleNextEvent(scanner))
			;
	}

	private boolean handleNextEvent(Scanner scanner) {
		waitForInput(scanner);
		String input = getInput(scanner);
		boolean quit = shouldQuit(input);
		if (!quit)
			writeResultForInput(input);
		return !quit;
	}

	private void waitForInput(Scanner scanner) {
		while (!scanner.hasNext())
			;
	}

	private String getInput(Scanner scanner) {
		return scanner.nextLine();
	}


	private boolean shouldQuit(String input) {
		return "quit".equals(input);
	}

	private void writeResultForInput(String input) {
		try {
			int result = resultForInput(input);
			System.out.println(result);
		} catch (RuntimeException e) {
			System.err.println("Invalid input: " + input);
		}
	}

	private int resultForInput(String input) {
		int sum = 0;
		for (String summand : input.split("\\+"))
			sum += parseInt(summand);
		return sum;
	}
}
