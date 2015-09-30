package org.junit.contrib.java.lang.system;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static com.github.stefanbirkner.fishbowl.Fishbowl.ignoreException;
import static com.github.stefanbirkner.fishbowl.Fishbowl.wrapCheckedException;

class Executor {
	private static final Description DUMMY_DESCRIPTION = null;

	static Throwable exceptionThrownWhenTestIsExecutedWithRule(
			final Statement test,  final TestRule rule) {
		return exceptionThrownBy(executeTestWithRuleRaw(test, rule));
	}

	static void executeTestWithRule(Statement test, TestRule rule) {
		//avoid `throws Throwable` for every test case declaration
		wrapCheckedException(executeTestWithRuleRaw(test, rule));
	}

	static void executeFailingTestWithRule(Statement test, TestRule rule) {
		ignoreException(
			executeTestWithRuleRaw(test, rule),
			AssertionError.class);
	}

	private static com.github.stefanbirkner.fishbowl.Statement executeTestWithRuleRaw(
			final Statement test, final TestRule rule) {
		return new com.github.stefanbirkner.fishbowl.Statement() {
			public void evaluate() throws Throwable {
				rule.apply(test, DUMMY_DESCRIPTION).evaluate();
			}
		};
	}
}
