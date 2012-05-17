package org.junit.contrib.java.lang.system.example;

import java.util.Scanner;

public class Summarize {
	public static int sumOfNumbersFromSystemIn() {
		Scanner scanner = new Scanner(System.in);
		int firstSummand = scanner.nextInt();
		int secondSummand = scanner.nextInt();
		return firstSummand + secondSummand;
	}
}
