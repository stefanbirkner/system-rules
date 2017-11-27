package org.junit.contrib.java.lang.system;

import static java.lang.System.getSecurityManager;
import static org.assertj.core.api.Assertions.assertThat;

import java.security.Permission;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class ProvideSecurityManagerTest {
	private static final SecurityManager MANAGER = new SecurityManager() {
		@Override
		public void checkPermission(Permission perm) {
			// everything is allowed
		}
	};

	public static class provided_security_manager_is_present_during_test {
		@Rule
		public final ProvideSecurityManager rule = new ProvideSecurityManager(MANAGER);

		@Test
		public void test() {
			assertThat(getSecurityManager()).isSameAs(MANAGER);
		}
	}

	@RunWith(AcceptanceTestRunner.class)
	public static class after_test_security_manager_is_the_same_as_before {
		private static final SecurityManager ORIGINAL_MANAGER = getSecurityManager();

		public static class TestClass {
			@Rule
			public final ProvideSecurityManager rule = new ProvideSecurityManager(MANAGER);

			@Test
			public void test() {
			}
		}

		public static void verifyStateAfterTest() {
			assertThat(getSecurityManager()).isSameAs(ORIGINAL_MANAGER);
		}
	}
}
