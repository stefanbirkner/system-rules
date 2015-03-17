package org.junit.contrib.java.lang.system;

/**
 * @deprecated This enum is no longer needed, because all rules that are using
 * it have been replaced with rules that don't need the enum.
 *
 * <p>Mode of the
 * {@link org.junit.contrib.java.lang.system.StandardErrorStreamLog} and the
 * {@link org.junit.contrib.java.lang.system.StandardOutputStreamLog}.
 */
@Deprecated
public enum LogMode {
	/**
	 * @deprecated Please use
	 * {@link SystemErrRule#enableLog()}.{@link SystemErrRule#mute() mute()} or
	 * {@link SystemOutRule#enableLog()}.{@link SystemOutRule#mute() mute()}.
	 *
	 * <p>Capture the writes to the stream. Nothing is written to the stream
	 * itself.
	 */
	LOG_ONLY,

	/**
	 * @deprecated Please use {@link SystemErrRule#enableLog()} or
	 * {@link SystemOutRule#enableLog()}.
	 *
	 * <p>Record the writes while they are still written to the stream.
	 */
	LOG_AND_WRITE_TO_STREAM
}
