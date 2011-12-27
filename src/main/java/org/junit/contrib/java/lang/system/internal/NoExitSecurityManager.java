package org.junit.contrib.java.lang.system.internal;

import java.security.Permission;


public class NoExitSecurityManager extends SecurityManager {
	@Override
	public void checkPermission(Permission perm) {
		// allow anything.
	}

	@Override
	public void checkExit(int status) {
		throw new CheckExitCalled(status);
	}
}