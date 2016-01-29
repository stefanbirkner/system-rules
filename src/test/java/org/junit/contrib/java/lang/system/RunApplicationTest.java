package org.junit.contrib.java.lang.system;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Executor.exceptionThrownWhenTestIsExecutedWithRule;
import static org.junit.contrib.java.lang.system.Executor.executeFailingTestWithRule;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;

public class RunApplicationTest {
	public static class Counter {
		static boolean STARTED = false;
		static boolean STOPPED = false;

		public static void main(String... arguments) throws InterruptedException {
			STARTED = true;
			while (!Thread.interrupted());
			STOPPED = true;
		}
	}

	@Test
	public void application_is_started_on_request() throws Exception {
		Counter.STARTED = false;
		final RunApplication runApplication = new RunApplication();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				runApplication.startApplication(Counter.class);
				assertThat(Counter.STARTED).isTrue();
			}
		}, runApplication);
	}

	@Test
	public void application_is_stopped_after_end_of_successful_test() throws Exception {
		Counter.STOPPED = false;
		final RunApplication runApplication = new RunApplication();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				runApplication.startApplication(Counter.class);
			}
		}, runApplication);
		assertThat(Counter.STOPPED).isTrue();
	}

	@Test
	public void application_is_stopped_when_test_throws_Exception() throws Exception {
		Counter.STOPPED = false;
		final RunApplication runApplication = new RunApplication();
		executeFailingTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				runApplication.startApplication(Counter.class);
				throw new AssertionError();
			}
		}, runApplication);
		assertThat(Counter.STOPPED).isTrue();
	}

	static class ApplicationThatCannotBeStopped {
		public static void main(String[] args) {
			while(true);
		}
	}

	@Test
	public void rule_warns_if_application_cannot_be_stopped() throws Exception {
		final RunApplication runApplication = new RunApplication();
		Throwable exception = exceptionThrownWhenTestIsExecutedWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				runApplication.startApplication(ApplicationThatCannotBeStopped.class);
			}
		}, runApplication);
		assertThat(exception).hasMessage(
			"The application cannot be stopped. Beware that it is still running.");
	}
}
