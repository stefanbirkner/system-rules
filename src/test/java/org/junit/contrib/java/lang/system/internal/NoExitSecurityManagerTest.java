package org.junit.contrib.java.lang.system.internal;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

import java.io.FileDescriptor;
import java.lang.reflect.Method;
import java.util.*;

import com.github.stefanbirkner.fishbowl.Statement;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.invocation.Invocation;

@RunWith(Enclosed.class)
public class NoExitSecurityManagerTest {

	@RunWith(Parameterized.class)
	public static class tests_common_to_both_scenarios {
		private static final int DUMMY_STATUS = 1;

		@Parameters(name = "{0}")
		public static List<Object[]> data() {
			return asList(
				new Object[] {
					"with_original_SecurityManager",
					mock(SecurityManager.class)
				},
				new Object[] {
					"without_original_SecurityManager",
					null
				}
			);
		}

		private final NoExitSecurityManager securityManager;

		public tests_common_to_both_scenarios(
				String name, SecurityManager originalManager) {
			securityManager = new NoExitSecurityManager(originalManager);
		}

		@Test
		public void an_exception_with_the_status_is_thrown_when_checkExit_is_called() {
			CheckExitCalled exception = exceptionThrownBy(new Statement() {
				public void evaluate() {
					securityManager.checkExit(DUMMY_STATUS);
				}
			}, CheckExitCalled.class);
			assertThat(exception.getStatus()).isEqualTo(DUMMY_STATUS);
		}

		@Test
		public void information_about_a_missing_checkExit_call_is_available() {
			assertThat(securityManager.isCheckExitCalled()).isFalse();
		}

		@Test
		public void information_about_a_checkExit_call_is_available() {
			safeCallCheckExitWithStatus(DUMMY_STATUS);
			assertThat(securityManager.isCheckExitCalled()).isTrue();
		}

		@Test
		public void status_of_first_call_of_checkExit_is_available() {
			safeCallCheckExitWithStatus(DUMMY_STATUS);
			safeCallCheckExitWithStatus(DUMMY_STATUS + 1);
			assertThat(securityManager.getStatusOfFirstCheckExitCall())
				.isEqualTo(DUMMY_STATUS);
		}

		private void safeCallCheckExitWithStatus(int status) {
			try {
				securityManager.checkExit(status);
			} catch (CheckExitCalled ignored) {
			}
		}

		@Test
		public void fails_to_provide_status_of_first_checkExit_call_if_this_call_did_not_happen() {
			Throwable exception = exceptionThrownBy(new Statement() {
				public void evaluate() {
					securityManager.getStatusOfFirstCheckExitCall();
				}
			});
			assertThat(exception)
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("checkExit(int) has not been called.");
		}
	}

	@RunWith(Parameterized.class)
	public static class public_methods_override {

		@Parameters(name = "{0}")
		public static List<Object[]> data() {
			List<Object[]> methods = new ArrayList<Object[]>();
			for (Method method : NoExitSecurityManager.class.getMethods())
				if (notDeclaredByObjectClass(method))
					methods.add(new Object[] { testName(method), method });
			return methods;
		}

		@Parameter(0)
		public String methodName;

		@Parameter(1)
		public Method method;

		@Test
		public void is_implemented_by_NoExitSecurityManager() {
			assertThat(method.getDeclaringClass())
				.isEqualTo(NoExitSecurityManager.class);
		}
	}

	@RunWith(Parameterized.class)
	public static class public_void_methods {
		@Parameters(name = "{0}")
		public static List<Object[]> data() {
			List<Object[]> methods = new ArrayList<Object[]>();
			for (Method method : NoExitSecurityManager.class.getMethods()) {
				if (voidMethod(method)
					&& notDeclaredByObjectClass(method)
					&& notChangedByNoExitSecurityManager(method))
					methods.add(new Object[] { testName(method), method });
			}
			return methods;
		}

		@Parameter(0)
		public String testName;

		@Parameter(1)
		public Method method;

		@Test
		public void may_be_called_when_original_security_manager_is_missing(
		) throws Exception {
			SecurityManager manager = new NoExitSecurityManager(null);
			method.invoke(manager, dummyArguments());
		}

		@Test
		public void is_delegated_to_original_security_manager_when_it_is_present(
		) throws Exception {
			SecurityManager originalManager = mock(SecurityManager.class);
			SecurityManager manager = new NoExitSecurityManager(
				originalManager);
			Object[] arguments = dummyArguments();
			method.invoke(manager, arguments);
			assertCallIsDelegated(originalManager, arguments);
		}

		private Object[] dummyArguments() {
			Class<?>[] parameterTypes = method.getParameterTypes();
			Object[] args = new Object[parameterTypes.length];
			for (int i = 0; i < args.length; ++i)
				args[i] = dummy(parameterTypes[i]);
			return args;
		}

		private Object dummy(Class<?> type) {
			if (type.getName().equals("int"))
				return new Random().nextInt();
			else if (type.getName().equals("byte"))
				return (byte) new Random().nextInt();
			else if (type.equals(String.class))
				return UUID.randomUUID().toString();
			else if (type.equals(Class.class))
				return String.class;
			else if (type.equals(FileDescriptor.class))
				return new FileDescriptor();
			else
				return mock(type);
		}

		private void assertCallIsDelegated(
			SecurityManager target, Object[] arguments) {
			Collection<Invocation> invocations = mockingDetails(target)
				.getInvocations();
			assertThat(invocations).hasSize(1);
			Invocation invocation = invocations.iterator().next();
			assertThat(invocation.getMethod())
				.isEqualToComparingOnlyGivenFields(
					method, "name", "parameterTypes", "returnType");
			assertThat(invocation.getRawArguments()).containsExactly(arguments);
		}
	}

	public static class with_original_SecurityManager {
		private final SecurityManager originalSecurityManager = mock(SecurityManager.class);
		private final NoExitSecurityManager managerWithOriginal = new NoExitSecurityManager(
			originalSecurityManager);

		@Test
		public void getInCheck_is_delegated_to_original_security_manager() {
			when(originalSecurityManager.getInCheck()).thenReturn(true);
			assertThat(managerWithOriginal.getInCheck()).isTrue();
		}

		@Test
		public void security_context_of_original_security_manager_is_provided() {
			Object context = new Object();
			when(originalSecurityManager.getSecurityContext()).thenReturn(context);
			assertThat(managerWithOriginal.getSecurityContext()).isSameAs(context);
		}

		@Test
		public void checkTopLevelWindow_is_delegated_to_original_security_manager() {
			Object window = new Object();
			when(originalSecurityManager.checkTopLevelWindow(window)).thenReturn(true);
			assertThat(managerWithOriginal.checkTopLevelWindow(window)).isTrue();
		}

		@Test
		public void thread_group_of_original_security_manager_is_provided() {
			ThreadGroup threadGroup = new ThreadGroup("dummy name");
			when(originalSecurityManager.getThreadGroup()).thenReturn(threadGroup);
			assertThat(managerWithOriginal.getThreadGroup()).isSameAs(threadGroup);
		}
	}

	public static class without_original_SecurityManager {
		private final NoExitSecurityManager managerWithoutOriginal = new NoExitSecurityManager(null);

		@Test
		public void getInCheck_returns_false() {
			assertThat(managerWithoutOriginal.getInCheck()).isFalse();
		}

		@Test
		public void getSecurityContext_may_be_called() {
			managerWithoutOriginal.getSecurityContext();
		}

		@Test
		public void checkTopLevelWindow_may_be_called() {
			Object window = new Object();
			managerWithoutOriginal.checkTopLevelWindow(window);
		}

		@Test
		public void getThreadGroup_may_be_called() {
			managerWithoutOriginal.getThreadGroup();
		}
	}

	private static boolean notChangedByNoExitSecurityManager(Method method) {
		return !method.getName().equals("checkExit");
	}

	private static boolean notDeclaredByObjectClass(Method method) {
		return !method.getDeclaringClass().equals(Object.class);
	}

	private static boolean voidMethod(Method method) {
		return method.getReturnType().getName().equals("void");
	}

	private static String testName(Method method) {
		return method.getName()
			+ "(" + join(method.getParameterTypes()) + ")";
	}

	private static String join(Class<?>[] types) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < types.length; i++) {
			if (i != 0)
				sb.append(",");
			sb.append(types[i].getSimpleName());
		}
		return sb.toString();
	}
}
