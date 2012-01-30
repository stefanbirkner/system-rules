package org.junit.contrib.java.lang.system;

import static java.lang.System.clearProperty;
import static java.lang.System.getProperty;
import static java.lang.System.setProperty;
import static org.apache.commons.io.IOUtils.copy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.contrib.java.lang.system.ProvideSystemProperty.fromFile;
import static org.junit.contrib.java.lang.system.ProvideSystemProperty.fromResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ProvideSystemPropertyTest {
	private static final String EXAMPLE_PROPERTIES = "example.properties";
	private static final Description NO_DESCRIPTION = null;
	private static final String ARBITRARY_NAME = "arbitrary property";
	private static final String ANOTHER_PROPERTY = "another property";
	private static final String ARBITRARY_VALUE = "arbitrary value";
	private static final String A_DIFFERENT_VALUE = "different value";

	private ProvideSystemProperty rule;

	@Rule
	public final RestoreSystemProperties restoreSystemProperty = new RestoreSystemProperties(
			ARBITRARY_NAME, ANOTHER_PROPERTY);

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void removeProperty() throws Throwable {
		setProperty(ARBITRARY_NAME, ARBITRARY_VALUE);
		rule = new ProvideSystemProperty(ARBITRARY_NAME, null);
		evaluateAssertPropertyWithNameAndValue(ARBITRARY_NAME, null);
	}

	@Test
	public void restoreOriginalValue() throws Throwable {
		setProperty(ARBITRARY_NAME, A_DIFFERENT_VALUE);
		evaluateStatementWithArbitraryValue();
		assertThat(getProperty(ARBITRARY_NAME), is(equalTo(A_DIFFERENT_VALUE)));
	}

	@Test
	public void removeValueIfNotPresentBefore() throws Throwable {
		clearProperty(ARBITRARY_NAME);
		evaluateStatementWithArbitraryValue();
		assertThat(getProperty(ARBITRARY_NAME), is(nullValue()));
	}

	@Test
	public void providesMultipleProperties() throws Throwable {
		rule = new ProvideSystemProperty(ARBITRARY_NAME, ARBITRARY_VALUE).and(
				ANOTHER_PROPERTY, A_DIFFERENT_VALUE);
		evaluateAssertPropertyWithNameAndValue(ARBITRARY_NAME, ARBITRARY_VALUE);
		evaluateAssertPropertyWithNameAndValue(ANOTHER_PROPERTY,
				A_DIFFERENT_VALUE);
	}

	@Test
	public void providePropertyFromResource() throws Throwable {
		rule = fromResource(EXAMPLE_PROPERTIES);
		evaluateAssertPropertyWithNameAndValue(ARBITRARY_NAME, ARBITRARY_VALUE);
	}

	@Test
	public void providePropertyFromFile() throws Throwable {
		File file = temporaryFolder.newFile();
		copyResourceToFile(EXAMPLE_PROPERTIES, file);
		rule = fromFile(file.getAbsolutePath());
		evaluateAssertPropertyWithNameAndValue(ARBITRARY_NAME, ARBITRARY_VALUE);
	}

	@Test
	public void restoresMultipleProperties() throws Throwable {
		setProperty(ANOTHER_PROPERTY, ARBITRARY_VALUE);

		rule = new ProvideSystemProperty(ARBITRARY_NAME, ARBITRARY_VALUE).and(
				ANOTHER_PROPERTY, A_DIFFERENT_VALUE);
		evaluateAssertPropertyWithNameAndValue(ANOTHER_PROPERTY,
				A_DIFFERENT_VALUE);

		assertThat(getProperty(ARBITRARY_NAME), is(nullValue()));
		assertThat(getProperty(ANOTHER_PROPERTY), is(ARBITRARY_VALUE));
	}

	private void evaluateStatementWithArbitraryValue() throws Throwable {
		rule = new ProvideSystemProperty(ARBITRARY_NAME, ARBITRARY_VALUE);
		evaluateAssertPropertyWithNameAndValue(ARBITRARY_NAME, ARBITRARY_VALUE);
	}

	private void evaluateAssertPropertyWithNameAndValue(String name,
			String value) throws Throwable {
		AssertValue assertValue = new AssertValue(name, value);
		evaluateRuleForStatement(assertValue);
	}

	private void evaluateRuleForStatement(Statement statement) throws Throwable {
		Statement ruleWithStatement = rule.apply(statement, NO_DESCRIPTION);
		ruleWithStatement.evaluate();
	}

	private void copyResourceToFile(String name, File file) throws Exception {
		FileOutputStream fos = new FileOutputStream(file);
		InputStream is = getClass().getResourceAsStream(name);
		copy(is, fos);
	}

	private static class AssertValue extends Statement {
		final String name;
		final String value;

		AssertValue(String name, String value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public void evaluate() {
			assertThat(getProperty(name), is(equalTo(value)));
		}
	}
}
