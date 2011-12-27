package org.junit.contrib.java.lang.system.internal;

public class CheckExitCalled extends SecurityException {
	private static final long serialVersionUID = 159678654L;

	private final Integer status;

	public CheckExitCalled(int status) {
		super("Tried to exit with status " + status + ".");
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}
}