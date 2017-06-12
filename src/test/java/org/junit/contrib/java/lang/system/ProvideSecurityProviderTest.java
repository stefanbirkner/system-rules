package org.junit.contrib.java.lang.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.Executor.executeTestWithRule;

import java.security.Provider;
import java.security.Security;

import org.junit.Test;
import org.junit.runners.model.Statement;

import sun.security.jca.Providers;

public class ProvideSecurityProviderTest {
	@Test
	public void rule_is_added() {
		ProvideSecurityProvider rule = new ProvideSecurityProvider(new TestProvider());
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				assertThat(Providers.getProviderList().getProvider("test"))
					.isNotNull()
					.isInstanceOf(TestProvider.class);
			}
		}, rule);
	}

	@Test
	public void rule_is_removed_if_new() {
		ProvideSecurityProvider rule = new ProvideSecurityProvider(new TestProvider());
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
			}
		}, rule);
		assertThat(Providers.getProviderList().getProvider("test"))
			.isNull();
	}

	@Test
	public void rule_is_not_removed_if_not_new() {
		TestProvider provider = new TestProvider();
		ProvideSecurityProvider rule = new ProvideSecurityProvider(new TestProvider());
		int position = Security.addProvider(provider);
		assertThat(position).isNotEqualTo(-1);
		executeTestWithRule(new Statement() {
			@Override
			public void evaluate() throws Throwable {
			}
		}, rule);
		assertThat(Providers.getProviderList().getProvider("test"))
			.isNotNull()
			.isInstanceOf(TestProvider.class);
		Security.removeProvider("test");
	}
}
