package org.junit.contrib.java.lang.system;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * The {@code RunApplication} rule starts a Java application and allows you to
 * communicate with this application through System.in, System.err and System.out.
 * At the end it kills the application by shutting down the thread that the
 * application is running in.
 */
public class RunApplication implements TestRule {
	private final ExecutorService executor = newSingleThreadExecutor();

	public void startApplication(final Class<?> application) {
		StartApplication startApplication = new StartApplication(application);
		executor.execute(startApplication);
		waitUntilApplicationIsStarted(startApplication);
	}

	private void waitUntilApplicationIsStarted(StartApplication startApplication) {
		while (!startApplication.immediatelyBeforeStart);
		try {
			Thread.currentThread().join(1);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					base.evaluate();
				} finally {
					executor.shutdownNow();
					boolean terminated = executor.awaitTermination(1, TimeUnit.SECONDS);
					if (!terminated)
						throw new IllegalStateException(
							"The application cannot be stopped. Beware that it is still running.");
				}
			}
		};
	}

	private static class StartApplication implements Runnable {
		final Class<?> application;
		boolean immediatelyBeforeStart;

		StartApplication(Class<?> application) {
			this.application = application;
		}

		public void run() {
			final Method main = getMainMethod(application);
			try {
				immediatelyBeforeStart = true;
				main.invoke(null, new Object[] { new String[0] });
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		Method getMainMethod(Class<?> application) {
			try {
				return application.getDeclaredMethod("main", String[].class);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
