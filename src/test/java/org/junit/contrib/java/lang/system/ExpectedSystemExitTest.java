package org.junit.contrib.java.lang.system;

import static java.lang.System.getSecurityManager;
import static java.lang.System.setSecurityManager;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

import java.security.Permission;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.Statement;


public class ExpectedSystemExitTest {
	private static final Object ARBITRARY_CONTEXT = new Object();

	@Rule
	public final ExpectedException thrown = none();

	private final ExpectedSystemExit rule = ExpectedSystemExit.none();

	@Test
	public void succeedWithoutExit() throws Throwable {
		executeRuleWithoutExitCall();
	}

	@Test
	public void succeedOnExitWithArbitraryStatusCode() throws Throwable {
		rule.expectSystemExit();
		executeRuleWithExitStatus0();
	}

	@Test
	public void succeedOnExitWithSelectedStatusCode() throws Throwable {
		rule.expectSystemExitWithStatus(0);
		executeRuleWithExitStatus0();
	}

	@Test
	public void failForUnexpectedSystemExit() throws Throwable {
		thrown.expectMessage("Unexpected call of System.exit(0).");
		executeRuleWithExitStatus0();
	}

	@Test
	public void failBecauseOfMissingSystemExitCall() throws Throwable {
		thrown.expectMessage("System.exit has not been called.");
		rule.expectSystemExit();
		executeRuleWithoutExitCall();
	}

	@Test
	public void failForWrongStatus() throws Throwable {
		thrown.expectMessage("Wrong exit status");
		rule.expectSystemExitWithStatus(1);
		executeRuleWithExitStatus0();
	}

	@Test
	public void restoreOldSecurityManager() throws Throwable {
		SecurityManager manager = new ArbitrarySecurityManager();
		setSecurityManager(manager);
		executeRuleWithoutExitCall();
		assertThat(getSecurityManager(), sameInstance(manager));
	}

	@Test
	public void delegateToOldSecurityManager() throws Throwable {
		SecurityManager manager = new ArbitrarySecurityManager();
		setSecurityManager(manager);
		executeRuleWithStatement(new CheckContext());
	}

	private void executeRuleWithoutExitCall() throws Throwable {
		executeRuleWithStatement(new EmptyStatement());
	}

	private void executeRuleWithExitStatus0() throws Throwable {
		executeRuleWithStatement(new SystemExit0());
	}

	private void executeRuleWithStatement(Statement statement) throws Throwable {
		rule.apply(statement, null).evaluate();
	}

	private static class SystemExit0 extends Statement {
		@Override
		public void evaluate() throws Throwable {
			System.exit(0);
		}
	}

	private static class CheckContext extends Statement {
		@Override
		public void evaluate() throws Throwable {
			assertEquals(ARBITRARY_CONTEXT, getSecurityManager()
					.getSecurityContext());
		}
	}

	private static class ArbitrarySecurityManager extends SecurityManager {
		@Override
		public Object getSecurityContext() {
			return ARBITRARY_CONTEXT;
		}

		@Override
		public void checkPermission(Permission perm) {
			// allow anything.
		}
	}
}