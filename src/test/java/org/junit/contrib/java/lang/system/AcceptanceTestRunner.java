package org.junit.contrib.java.lang.system;

import org.junit.*;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

public class AcceptanceTestRunner extends BlockJUnit4ClassRunner {
	private static final Collection<Failure> NO_FAILURES = emptyList();
	private final Method expectFailure;
	private final Method verifyStateAfterTest;
	private final Method verifyResult;
	private final TestClass testClass;

	public AcceptanceTestRunner(Class<?> testClass) throws InitializationError {
		super(extractInnerTestClass(testClass));
		expectFailure = extractMethod(testClass, "expectFailure", Failure.class);
		verifyResult = extractMethod(testClass, "verifyResult", Collection.class);
		verifyStateAfterTest = extractMethod(testClass, "verifyStateAfterTest");
		this.testClass = new TestClass(testClass);
		verifyCheckPresent();
	}

	private static Class<?> extractInnerTestClass(Class<?> testClass)
			throws InitializationError {
		Class<?>[] innerClasses = testClass.getClasses();
		if (innerClasses.length > 1)
			throw new InitializationError("The class " + testClass
				+ " has " + innerClasses.length + " inner classes, but only"
				+ " one inner class with name TestClass is expected.");
		else if (innerClasses.length == 0
				|| !innerClasses[0].getSimpleName().equals("TestClass"))
			throw new InitializationError("The class " + testClass
				+ " has no inner class with name TestClass.");
		else 
			return innerClasses[0];
	}

	private void verifyCheckPresent() {
		boolean noCheck = expectFailure == null
			&& verifyResult == null
			&& verifyStateAfterTest == null;
		if (noCheck)
			throw new IllegalStateException(
				"No expectation is defined for the test " + getName()
					+ ". It needs either a method expectFailure, verifyResult or verifyStateAfterTest.");
	}

	private static Method extractMethod(Class<?> testClass, String name, Class<?>... parameterTypes) {
		try {
			return testClass.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	@Override
	protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
		Description description = describeChild(method);
		if (method.getAnnotation(Ignore.class) != null)
			notifier.fireTestIgnored(description);
		else
			runTest(methodBlock(method), description, notifier);
	}

	protected Statement classBlock(final RunNotifier notifier) {
		Statement statement = super.classBlock(notifier);
		statement = withBeforeClasses(statement);
		statement = withAfterClasses(statement);
		statement = withClassRules(statement);
		return statement;
	}

	protected Statement withBeforeClasses(Statement statement) {
		List<FrameworkMethod> befores = testClass
			.getAnnotatedMethods(BeforeClass.class);
		return befores.isEmpty() ? statement :
			new RunBefores(statement, befores, null);
	}

	protected Statement withAfterClasses(Statement statement) {
		List<FrameworkMethod> afters = testClass
			.getAnnotatedMethods(AfterClass.class);
		return afters.isEmpty() ? statement :
			new RunAfters(statement, afters, null);
	}

	private Statement withClassRules(Statement statement) {
		List<TestRule> classRules = classRules();
		return classRules.isEmpty() ? statement :
			new RunRules(statement, classRules, getDescription());
	}

	protected List<TestRule> classRules() {
		return testClass.getAnnotatedFieldValues(
			null, ClassRule.class, TestRule.class);
	}

	private void runTest(Statement statement, Description description,
			RunNotifier notifier) {
		EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
		eachNotifier.fireTestStarted();
		try {
			statement.evaluate();
			handleNoFailure(eachNotifier);
		} catch (AssumptionViolatedException e) {
			handleFailedAssumption(description, eachNotifier, e);
		} catch (Throwable e) {
			handleException(description, eachNotifier, e);
		} finally {
			invokeIfPresent(verifyStateAfterTest, eachNotifier);
			eachNotifier.fireTestFinished();
		}
	}

	private void handleNoFailure(EachTestNotifier eachNotifier) {
		if (expectFailure != null)
			eachNotifier.addFailure(new AssertionError("Test did not fail."));
		invokeIfPresent(verifyResult, eachNotifier, NO_FAILURES);
	}

	private void handleFailedAssumption(Description description, EachTestNotifier eachNotifier, 			AssumptionViolatedException e) {
		eachNotifier.addFailedAssumption(e);
		if (expectFailure != null)
			eachNotifier.addFailure(new AssertionError("Test did not fail."));
		invokeIfPresent(
			verifyResult, eachNotifier, singleton(new Failure(description, e)));
	}

	private void handleException(Description description, EachTestNotifier eachNotifier,
			Throwable e) {
		invokeIfPresent(
			verifyResult, eachNotifier, singleton(new Failure(description, e)));
		invokeIfPresent(
			expectFailure, eachNotifier, new Failure(description, e));
		if (expectFailure == null && verifyResult == null)
			eachNotifier.addFailure(e);
	}

	private void invokeIfPresent(Method method, EachTestNotifier notifier, Object... args) {
		if (method != null)
			try {
				method.invoke(null, args);
			} catch (IllegalAccessException e) {
				fail(notifier, e, "Failed to invoke '" + method.getName() + "'.");
			} catch (InvocationTargetException e) {
				fail(notifier, e, "Failed to invoke '" + method.getName() + "'.");
			} catch (Exception e) {
				notifier.addFailure(e);
			}
	}

	private void fail(EachTestNotifier eachNotifier, InvocationTargetException e, String message) {
		if (e.getCause() instanceof AssertionError)
			eachNotifier.addFailure(e.getCause());
		else
			fail(eachNotifier, (Exception) e, message);
	}

	private void fail(EachTestNotifier eachNotifier, Exception e, String message) {
		AssertionError error = new AssertionError(message);
		error.initCause(e);
		eachNotifier.addFailure(error);
	}
}
