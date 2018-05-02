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
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Enclosed.class)
public class EnvironmentVariablesTest {
	@RunWith(Enclosed.class)
	public static class modification_before_the_test {
		@RunWith(AcceptanceTestRunner.class)
		public static class after_a_successful_test_environment_variables_map_contains_same_values_as_before {
			private static Map<String, String> originalEnvironmentVariables;

			@BeforeClass
			public static void captureEnviromentVariables() {
				originalEnvironmentVariables = new HashMap<String, String>(getenv());
			}

			public static class TestClass {
				@Rule
				public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
					.set("dummy name", randomValue());

				@Test
				public void test() {
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
				public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
					.set("dummy name", randomValue());

				@Test
				public void test() {
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
				public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
					.set("dummy name", randomValue());

				@Test
				public void test() {
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
				public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
					.set("dummy name", randomValue());

				@Test
				public void test() {
					throw new RuntimeException("dummy exception");
				}
			}

			public static void expectFailure(Failure failure) {
				assertThat(getenv("dummy name")).isEqualTo(originalValue);
			}
		}

		public static class environment_variable_that_is_set_by_the_rule_is_available_in_the_test {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
				.set("dummy name", "dummy value");

			@Test
			public void test() {
				assertThat(getenv("dummy name")).isEqualTo("dummy value");
			}
		}

		public static class environment_variable_that_is_set_by_the_rule_is_available_from_environment_variables_map {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
				.set("dummy name", "dummy value");

			@Test
			public void test() {
				assertThat(getenv()).containsEntry("dummy name", "dummy value");
			}
		}

		public static class environment_variable_that_is_set_to_null_by_the_rule_is_null_in_the_test {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
				//we need to set a value because it is null by default
				.set("dummy name", randomValue())
				.set("dummy name", null);

			@Test
			public void test() {
				assertThat(getenv("dummy name")).isNull();
			}
		}

		public static class environment_variable_that_is_set_to_null_by_the_rule_is_not_stored_in_the_environment_variables_map {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
				//we need to set a value because it is null by default
				.set("dummy name", randomValue())
				.set("dummy name", null);

			@Test
			public void test() {
				assertThat(getenv()).doesNotContainKey("dummy name");
			}
		}

		public static class environment_variables_that_are_cleared_by_the_rule_are_null_in_the_test {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
				//we need to set a value because it is null by default
				.set("dummy name", randomValue())
				.set("another name", randomValue())
				.clear("dummy name", "another name");

			@Test
			public void test() {
				assertThat(getenv("dummy name")).isNull();
				assertThat(getenv("another name")).isNull();
			}
		}

		public static class environment_variables_that_are_cleared_by_the_rule_are_not_stored_in_the_environment_variables_map {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
				//we need to set a value because it is null by default
				.set("dummy name", randomValue())
				.set("another name", randomValue())
				.clear("dummy name", "another name");

			@Test
			public void test() {
				assertThat(getenv())
					.doesNotContainKey("dummy name")
					.doesNotContainKey("another name");
			}
		}
	}

	@RunWith(Enclosed.class)
	public static class modification_within_the_test {
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
					environmentVariables.set("dummy name", randomValue());
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
					environmentVariables.set("dummy name", randomValue());
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
					environmentVariables.set("dummy name", randomValue());
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
					environmentVariables.set("dummy name", randomValue());
					throw new RuntimeException("dummy exception");
				}
			}

			public static void expectFailure(Failure failure) {
				assertThat(getenv("dummy name")).isEqualTo(originalValue);
			}
		}

		public static class environment_variable_that_is_set_by_the_rule_is_available_in_the_test {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

			@Test
			public void test() {
				environmentVariables.set("dummy name", "dummy value");
				assertThat(getenv("dummy name")).isEqualTo("dummy value");
			}
		}

		public static class environment_variable_that_is_set_by_the_rule_is_available_from_environment_variables_map {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

			@Test
			public void test() {
				environmentVariables.set("dummy name", "dummy value");
				assertThat(getenv()).containsEntry("dummy name", "dummy value");
			}
		}

		public static class environment_variable_that_is_set_to_null_by_the_rule_is_null_in_the_test {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

			@Test
			public void test() {
				//we need to set a value because it is null by default
				environmentVariables.set("dummy name", randomValue());
				environmentVariables.set("dummy name", null);
				assertThat(getenv("dummy name")).isNull();
			}
		}

		public static class environment_variable_that_is_set_to_null_by_the_rule_is_not_stored_in_the_environment_variables_map {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

			@Test
			public void test() {
				//we need to set a value because it is null by default
				environmentVariables.set("dummy name", randomValue());
				environmentVariables.set("dummy name", null);
				assertThat(getenv()).doesNotContainKey("dummy name");
			}
		}

		public static class environment_variables_that_are_cleared_by_the_rule_are_null_in_the_test {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

			@Test
			public void test() {
				//we need to set a value because it is null by default
				environmentVariables.set("dummy name", randomValue());
				environmentVariables.set("another name", randomValue());
				environmentVariables.clear("dummy name", "another name");
				assertThat(getenv("dummy name")).isNull();
				assertThat(getenv("another name")).isNull();
			}
		}

		public static class environment_variables_that_are_cleared_by_the_rule_are_not_stored_in_the_environment_variables_map {
			@Rule
			public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

			@Test
			public void test() {
				//we need to set a value because it is null by default
				environmentVariables.set("dummy name", randomValue());
				environmentVariables.set("another name", randomValue());
				environmentVariables.clear("dummy name", "another name");
				assertThat(getenv())
					.doesNotContainKey("dummy name")
					.doesNotContainKey("another name");
			}
		}
	}

	private static String randomValue() {
		return randomUUID().toString();
	}
}
