package org.junit.contrib.java.lang.system;


import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.getenv;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class EnvironmentVariablesTest {
	@RunWith(AcceptanceTestRunner.class)
	public static class after_a_successful_test_environment_variables_map_contains_same_values_as_before {
		private static Map<String, String> originalEnvironmentVariables;

		@BeforeClass
		public static void captureEnviromentVariables() {
			originalEnvironmentVariables = new HashMap<String, String>(getenv());
		}

		public static class TestClass {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

			@Test
			public void test() {
				environmentVariables.set("dummy name", "dummy value");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(getenv()).isEqualTo(originalEnvironmentVariables);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class after_a_successful_test_environment_variables_are_the_same_as_before {
		private static String originalValue;

		@BeforeClass
		public static void captureValue() {
			originalValue = getenv("dummy name");
		}

		public static class TestClass {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

			@Test
			public void test() {
				environmentVariables.set("dummy name", "dummy value");
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(getenv("dummy name")).isEqualTo(originalValue);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class after_a_test_that_throws_an_exception_environment_variables_map_contains_same_values_as_before {
		private static Map<String, String> originalEnvironmentVariables;

		@BeforeClass
		public static void captureEnviromentVariables() {
			originalEnvironmentVariables = new HashMap<String, String>(getenv());
		}

		public static class TestClass {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

			@Test
			public void test() {
				environmentVariables.set("dummy name", "dummy value");
				throw new RuntimeException("dummy exception");
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(getenv()).isEqualTo(originalEnvironmentVariables);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class after_a_test_that_throws_an_exception_environment_variables_are_the_same_as_before {
		private static String originalValue;

		@BeforeClass
		public static void captureValue() {
			originalValue = getenv("dummy name");
		}

		public static class TestClass {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

			@Test
			public void test() {
				environmentVariables.set("dummy name", "dummy value");
				throw new RuntimeException("dummy exception");
			}
		}

		public static void expectFailure(Failure failure) {
			assertThat(getenv("dummy name")).isEqualTo(originalValue);
		}
	}

	public static class environment_variable_that_is_set_in_the_test_is_available_in_the_test {
		@Rule
		public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

		@Test
		public void test() {
			environmentVariables.set("dummy name", "dummy value");
			assertThat(getenv("dummy name")).isEqualTo("dummy value");
		}
	}

	public static class environment_variable_that_is_set_in_the_test_is_available_from_environment_variables_map {
		@Rule
		public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

		@Test
		public void test() {
			environmentVariables.set("dummy name", "dummy value");
			assertThat(getenv()).containsEntry("dummy name", "dummy value");
		}
	}

	public static class environment_variable_that_is_set_to_null_in_the_test_is_null_in_the_test {
		@Rule
		public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

		@Test
		public void test() {
			//we need to set a value because it is null by default
			environmentVariables.set("dummy name", "dummy value");
			environmentVariables.set("dummy name", null);
			assertThat(getenv("dummy name")).isNull();
		}
	}

	public static class environment_variable_that_is_set_to_null_in_the_test_is_not_stored_in_the_environment_variables_map {
		@Rule
		public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

		@Test
		public void test() {
			//we need to set a value because it is null by default
			environmentVariables.set("dummy name", "dummy value");
			environmentVariables.set("dummy name", null);
			assertThat(getenv()).doesNotContainKey("dummy name");
		}
	}

	public static class environment_variables_that_are_cleared_in_the_test_are_null_in_the_test {
		@Rule
		public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

		@Test
		public void test() {
			//we need to set a value because it is null by default
			environmentVariables.set("dummy name", "dummy value");
			environmentVariables.set("another name", "dummy value");
			environmentVariables.clear("dummy name", "another name");
			assertThat(getenv("dummy name")).isNull();
			assertThat(getenv("another name")).isNull();
		}
	}

	public static class environment_variables_that_are_cleared_in_the_test_are_not_stored_in_the_environment_variables_map {
		@Rule
		public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

		@Test
		public void test() {
			//we need to set a value because it is null by default
			environmentVariables.set("dummy name", "dummy value");
			environmentVariables.set("another name", "dummy value");
			environmentVariables.clear("dummy name", "another name");
			assertThat(getenv())
				.doesNotContainKey("dummy name")
				.doesNotContainKey("another name");
		}
	}
}
