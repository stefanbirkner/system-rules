package org.junit.contrib.java.lang.system;

import java.security.Provider;
import java.security.Security;

import org.junit.rules.ExternalResource;

/**
 * The {@code ProvideSecurityProvider} rule provides an arbitrary security
 * provider to a test. After the test, the security provider is removed.
 *
 * <pre>
 *   public void MyTest {
 *     private final MySecurityProvider securityProvider
 *       = new MySecurityProvider();
 *
 *     &#064;Rule
 *     public final ProvideSecurityProvider provideSecurityProvider
 *       = new ProvideSecurityProvider(securityProvider);
 *
 *     &#064;Test
 *     public void useCustomDigestImplementation() {
 *       assertThat(MessageDigest.getInstance("SHA-3", "test")).isNotNull();
 *     }
 *   }
 * </pre>
 */
public class ProvideSecurityProvider extends ExternalResource {
	private int position;
	private final Provider provider;

	public ProvideSecurityProvider(Provider provider) {
		this.provider = provider;
	}
	@Override
	protected void before() throws Throwable {
		position = Security.addProvider(provider);
	}

	@Override
	protected void after() {
		if (position == -1) {
			return;
		}
		Security.removeProvider(provider.getName());
	}
}
