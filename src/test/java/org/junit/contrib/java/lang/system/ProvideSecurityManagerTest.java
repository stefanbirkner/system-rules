package org.junit.contrib.java.lang.system;

import static java.lang.System.getSecurityManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;
import static org.junit.contrib.java.lang.system.Statements.TEST_THAT_DOES_NOTHING;

import java.security.Permission;

import org.junit.Test;
import org.junit.runners.model.Statement;

public class ProvideSecurityManagerTest {
	private static final SecurityManager MANAGER = new SecurityManager() {
		@Override
		public void checkPermission(Permission perm) {
			// everything is allowed
		}
	};

	public ProvideSecurityManager rule = new ProvideSecurityManager(MANAGER);

	@Test
	public void provided_security_manager_is_present_during_test() {
		CaptureSecurityManager test = new CaptureSecurityManager();
		executeTestWithRule(test, rule);
		assertThat(test.securityManagerDuringTest).isSameAs(MANAGER);
	}

	@Test
	public void after_test_security_manager_is_the_same_as_before() {
		SecurityManager originalManager = getSecurityManager();
		executeTestWithRule(TEST_THAT_DOES_NOTHING, rule);
		assertThat(getSecurityManager()).isSameAs(originalManager);
	}

	private static class CaptureSecurityManager extends Statement {
		SecurityManager securityManagerDuringTest;

		@Override
		public void evaluate() throws Throwable {
			securityManagerDuringTest = getSecurityManager();
		}
	}
}
