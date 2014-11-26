package org.junit.contrib.java.lang.system;

import static java.lang.System.getSecurityManager;
import static java.lang.System.setSecurityManager;

import org.junit.rules.ExternalResource;

/**
 * The {@code ProvideSecurityManager} rule provides an arbitrary security
 * manager to a test. After the test the original security manager is restored.
 *
 * <pre>
 *   public void MyTest {
 *     private final MySecurityManager securityManager
 *       = new MySecurityManager();
 *
 *     &#064;Rule
 *     public final ProvideSecurityManager provideSecurityManager
 *       = new ProvideSecurityManager(securityManager);
 *
 *     &#064;Test
 *     public void overrideProperty() {
 *       assertEquals(securityManager, System.getSecurityManager());
 *     }
 *   }
 * </pre>
 */
public class ProvideSecurityManager extends ExternalResource {
	private final SecurityManager manager;
	private SecurityManager originalManager;

	public ProvideSecurityManager(SecurityManager manager) {
		this.manager = manager;
	}

	@Override
	protected void before() throws Throwable {
		originalManager = getSecurityManager();
		setSecurityManager(manager);
	}

	@Override
	protected void after() {
		setSecurityManager(originalManager);
	}
}
