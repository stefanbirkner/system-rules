package org.junit.contrib.java.lang.system;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runners.model.Statement;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.getenv;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Executor.executeTestThatThrowsExceptionWithRule;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;

public class EnvironmentVariablesTest {
	@Test
	public void after_a_successful_test_environment_variables_map_contains_same_values_as_before() {
		Map<String, String> originalEnvironmentVariables = new HashMap<String, String>(getenv());
		final EnvironmentVariables environmentVariables = new EnvironmentVariables();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				environmentVariables.set("dummy name", "dummy value");
			}
		}, environmentVariables);
		assertThat(getenv()).isEqualTo(originalEnvironmentVariables);
	}

	@Test
	public void after_a_successful_test_environment_variables_are_the_same_as_before() {
		String originalValue = getenv("dummy name");
		final EnvironmentVariables environmentVariables = new EnvironmentVariables();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				environmentVariables.set("dummy name", "dummy value");
			}
		}, environmentVariables);
		assertThat(getenv("dummy name")).isEqualTo(originalValue);
	}

	@Test
	public void after_a_test_that_throws_an_exception_environment_variables_map_contains_same_values_as_before() {
		Map<String, String> originalEnvironmentVariables = new HashMap<String, String>(getenv());
		final EnvironmentVariables environmentVariables = new EnvironmentVariables();
		executeTestThatThrowsExceptionWithRule(new Statement() {
			@Override
			public void evaluate() {
				environmentVariables.set("dummy name", "dummy value");
				throw new RuntimeException("dummy exception");
			}
		}, environmentVariables);
		assertThat(getenv()).isEqualTo(originalEnvironmentVariables);
	}

	@Test
	public void after_a_test_that_throws_an_exception_environment_variables_are_the_same_as_before() {
		String originalValue = getenv("dummy name");
		final EnvironmentVariables environmentVariables = new EnvironmentVariables();
		executeTestThatThrowsExceptionWithRule(new Statement() {
			@Override
			public void evaluate() {
				environmentVariables.set("dummy name", "dummy value");
				throw new RuntimeException("dummy exception");
			}
		}, environmentVariables);
		assertThat(getenv("dummy name")).isEqualTo(originalValue);
	}

	@Test
	public void environment_variable_that_is_set_in_the_test_is_available_in_the_test() {
		final EnvironmentVariables environmentVariables = new EnvironmentVariables();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				environmentVariables.set("dummy name", "dummy value");
				assertThat(getenv("dummy name")).isEqualTo("dummy value");
			}
		}, environmentVariables);
	}

	@Test
	public void environment_variable_that_is_set_in_the_test_is_available_from_environment_variables_map() {
		final EnvironmentVariables environmentVariables = new EnvironmentVariables();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				environmentVariables.set("dummy name", "dummy value");
				assertThat(getenv()).containsEntry("dummy name", "dummy value");
			}
		}, environmentVariables);
	}

	@Test
	public void environment_variable_that_is_set_to_null_in_the_test_is_null_in_the_test() {
		final EnvironmentVariables environmentVariables = new EnvironmentVariables();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				//we need to set a value because it is null by default
				environmentVariables.set("dummy name", "dummy value");
				environmentVariables.set("dummy name", null);
				assertThat(getenv("dummy name")).isNull();
			}
		}, environmentVariables);
	}

	@Test
	public void environment_variable_that_is_set_to_null_in_the_test_is_not_stored_in_the_environment_variables_map() {
		final EnvironmentVariables environmentVariables = new EnvironmentVariables();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				//we need to set a value because it is null by default
				environmentVariables.set("dummy name", "dummy value");
				environmentVariables.set("dummy name", null);
				assertThat(getenv()).doesNotContainKey("dummy name");
			}
		}, environmentVariables);
	}

	@Test
	public void environment_variables_can_be_set_when_instantiating_the_rule() {
		final EnvironmentVariables environmentVariables = EnvironmentVariables.builder()
			.with("k1", "v1")
			.with("k2", "v2")
			.without("PATH")
			.build();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				assertThat(getenv("k1")).isEqualTo("v1");
				assertThat(getenv("k2")).isEqualTo("v2");
				assertThat(getenv("PATH")).isNull();
			}
		}, environmentVariables);
		assertThat(getenv("k1")).isNull();
		assertThat(getenv("k2")).isNull();
		assertThat(getenv("PATH")).isNotNull();
	}

	@Test
	public void environment_variables_that_are_cleared_in_the_test_are_null_in_the_test() {
		final EnvironmentVariables environmentVariables = new EnvironmentVariables();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				//we need to set a value because it is null by default
				environmentVariables.set("dummy name", "dummy value");
				environmentVariables.set("another name", "dummy value");
				environmentVariables.clear("dummy name", "another name");
				assertThat(getenv("dummy name")).isNull();
				assertThat(getenv("another name")).isNull();
			}
		}, environmentVariables);
	}

	@Test
	public void environment_variables_that_are_cleared_in_the_test_are_not_stored_in_the_environment_variables_map() {
		final EnvironmentVariables environmentVariables = new EnvironmentVariables();
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() {
				//we need to set a value because it is null by default
				environmentVariables.set("dummy name", "dummy value");
				environmentVariables.set("another name", "dummy value");
				environmentVariables.clear("dummy name", "another name");
				assertThat(getenv())
					.doesNotContainKey("dummy name")
					.doesNotContainKey("another name");
			}
		}, environmentVariables);
	}
}
