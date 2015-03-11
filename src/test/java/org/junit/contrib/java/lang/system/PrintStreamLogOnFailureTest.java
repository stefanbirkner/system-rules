/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>.
 */

package org.junit.contrib.java.lang.system;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.lang.System.err;
import static java.lang.System.out;
import static java.lang.System.setErr;
import static java.lang.System.setOut;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

public class PrintStreamLogOnFailureTest {
	private static final String ARBITRARY_TEXT = "arbitrary text";

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void doesNotWriteToSystemOutputStreamForLogOnFailureModeWithoutFailure() throws Throwable {
		StandardOutputStreamLog log = new StandardOutputStreamLog(LogMode.LOG_AND_WRITE_TO_STREAM_ON_FAILURE_ONLY);
		PrintStream originalStream = out;
		try {
			ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
			executeRuleWithStatement(log, new WriteTextToStandardOutputStreamWithoutFailure());
			assertThat(captureOutputStream, hasToString(isEmptyString()));
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void doesWriteToOutputLogForLogOnFailureModeWithoutFailure() throws Throwable {
		StandardOutputStreamLog log = new StandardOutputStreamLog(LogMode.LOG_AND_WRITE_TO_STREAM_ON_FAILURE_ONLY);
		PrintStream originalStream = out;
		try {
			ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
			executeRuleWithStatement(log, new WriteTextToStandardOutputStreamWithoutFailure());
			assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void doesWriteToSystemOutputStreamForLogOnFailureModeWithFailure() throws Throwable {
		StandardOutputStreamLog log = new StandardOutputStreamLog(LogMode.LOG_AND_WRITE_TO_STREAM_ON_FAILURE_ONLY);
		PrintStream originalStream = out;
		ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
		try {
			setOut(new PrintStream(captureOutputStream));
			executeRuleWithStatement(log, new WriteTextToStandardOutputStreamWithFailure());
		} catch (IgnoredException ignored) {
			assertThat(captureOutputStream, hasToString(equalTo(ARBITRARY_TEXT)));
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void doesWriteToOutputLogForLogOnFailureModeWithFailure() throws Throwable {
		StandardOutputStreamLog log = new StandardOutputStreamLog(LogMode.LOG_AND_WRITE_TO_STREAM_ON_FAILURE_ONLY);
		PrintStream originalStream = out;
		try {
			ByteArrayOutputStream captureOutputStream = new ByteArrayOutputStream();
			setOut(new PrintStream(captureOutputStream));
			executeRuleWithStatement(log, new WriteTextToStandardOutputStreamWithFailure());
		} catch (IgnoredException ignored) {
			assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
		} finally {
			setOut(originalStream);
		}
	}

	@Test
	public void doesNotWriteToSystemErrorStreamForLogOnFailureModeWithoutFailure() throws Throwable {
		StandardErrorStreamLog log = new StandardErrorStreamLog(LogMode.LOG_AND_WRITE_TO_STREAM_ON_FAILURE_ONLY);
		PrintStream originalStream = err;
		try {
			ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
			executeRuleWithStatement(log, new WriteTextToStandardErrorStreamWithoutFailure());
			assertThat(captureErrorStream, hasToString(isEmptyString()));
		} finally {
			setErr(originalStream);
		}
	}

	@Test
	public void doesWriteToErrorLogForLogOnFailureModeWithoutFailure() throws Throwable {
		StandardErrorStreamLog log = new StandardErrorStreamLog(LogMode.LOG_AND_WRITE_TO_STREAM_ON_FAILURE_ONLY);
		PrintStream originalStream = err;
		try {
			ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
			executeRuleWithStatement(log, new WriteTextToStandardErrorStreamWithoutFailure());
			assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
		} finally {
			setErr(originalStream);
		}
	}

	@Test
	public void doesWriteToSystemErrorStreamForLogOnFailureModeWithFailure() throws Throwable {
		StandardErrorStreamLog log = new StandardErrorStreamLog(LogMode.LOG_AND_WRITE_TO_STREAM_ON_FAILURE_ONLY);
		PrintStream originalStream = err;
		ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
		try {
			setErr(new PrintStream(captureErrorStream));
			executeRuleWithStatement(log, new WriteTextToStandardErrorStreamWithFailure());
		} catch (IgnoredException ignored) {
			assertThat(captureErrorStream, hasToString(equalTo(ARBITRARY_TEXT)));
		} finally {
			setErr(originalStream);
		}
	}

	@Test
	public void doesWriteToErrorLogForLogOnFailureModeWithFailure() throws Throwable {
		StandardErrorStreamLog log = new StandardErrorStreamLog(LogMode.LOG_AND_WRITE_TO_STREAM_ON_FAILURE_ONLY);
		PrintStream originalStream = err;
		try {
			ByteArrayOutputStream captureErrorStream = new ByteArrayOutputStream();
			setErr(new PrintStream(captureErrorStream));
			executeRuleWithStatement(log, new WriteTextToStandardErrorStreamWithFailure());
		} catch (IgnoredException ignored) {
			assertThat(log.getLog(), is(equalTo(ARBITRARY_TEXT)));
		} finally {
			setErr(originalStream);
		}
	}

	private void executeRuleWithStatement(TestRule rule, Statement statement) throws Throwable {
		rule.apply(statement, null).evaluate();
	}

	private class WriteTextToStandardOutputStreamWithoutFailure extends Statement {
		@Override
		public void evaluate() throws Throwable {
			out.print(ARBITRARY_TEXT);
		}
	}

	private class WriteTextToStandardOutputStreamWithFailure extends Statement {
		@Override
		public void evaluate() throws Throwable {
			out.print(ARBITRARY_TEXT);
			throw new IgnoredException();
		}
	}

	private class WriteTextToStandardErrorStreamWithoutFailure extends Statement {
		@Override
		public void evaluate() throws Throwable {
			err.print(ARBITRARY_TEXT);
		}
	}

	private class WriteTextToStandardErrorStreamWithFailure extends Statement {
		@Override
		public void evaluate() throws Throwable {
			err.print(ARBITRARY_TEXT);
			throw new IgnoredException();
		}
	}

	private class IgnoredException extends Exception {
	}
}
