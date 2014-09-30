package org.junit.contrib.java.lang.system;

/**
 * Mode of the {@link org.junit.contrib.java.lang.system.StandardErrorStreamLog}
 * and the {@link org.junit.contrib.java.lang.system.StandardOutputStreamLog}.
 */
public enum LogMode {
	/**
	 * Capture the writes to the stream. Nothing is written to the stream itself.
	 */
	LOG_ONLY,

	/**
	 * Record the writes while they are still written to the stream.
	 */
	LOG_AND_WRITE_TO_STREAM
}
