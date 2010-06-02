package br.gov.serpro.tools.junit;

import static br.gov.serpro.tools.junit.GeneratorHelper.lowerCaseFirstChar;
import static br.gov.serpro.tools.junit.GeneratorHelper.upperCaseFirstChar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.FieldMethodInvocation;
import br.gov.serpro.tools.junit.model.Flow;
import br.gov.serpro.tools.junit.model.FormalParameter;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.model.Method;
import br.gov.serpro.tools.junit.model.Type;

public class TestCaseMethodFlowGenerator {
	final private Method method;
	final private Flow flow;
	final private JavaClass classUnderTest;
	final private String varNameForClassUnderTest;
	final private Set<Field> usedDependencies;

	public TestCaseMethodFlowGenerator(Flow flow, List<Field> dependencies) {
		this.flow = flow;
		this.method = flow.getMethod();
		this.classUnderTest = method.getJavaClass();
		this.varNameForClassUnderTest = classUnderTest.variableNameForType();
		this.usedDependencies = selectUsedDependencies(dependencies);
	}

	private Set<Field> selectUsedDependencies(List<Field> dependencies) {
		Set<Field> ret = new HashSet<Field>();
		for (FieldMethodInvocation invocation : flow.getInvocations()) {
			if (dependencies.contains(invocation.getInvokedAtField())) {
				ret.add(invocation.getInvokedAtField());
			}
		}
		return ret;
	}

	public String generate() {
		SourceBuilder sb = new SourceBuilder();
		sb.appendJavaDoc(String.format("Teste para o metodo {@link %s#%s}", method.getJavaClass()
				.getType(), method.getLoggingSignature()));

		final String usedVars = generateUsedVars();
		final String configMocks = generateConfigMocks();
		final String configInternalState = generateConfigInternalState();
		final String invokeMethod = generateInvokeMethod();
		final String checkMocks = generateCheckMocks();
		final String verifys = generateVerifys();

		sb.appendln(generateStartMethod());
		sb.appendln(usedVars);
		sb.appendln(configMocks);
		sb.appendln(configInternalState);
		sb.appendln(invokeMethod);
		sb.appendln(checkMocks);
		sb.appendln(verifys);
		sb.appendln(generateEndMethod());

		return sb.toString();
	}

	String generateEndMethod() {
		return "}";
	}

	String generateVerifys() {
		SourceBuilder sb = new SourceBuilder();
		sb.appendLineComment("verificacoes do resultado do metodo sendo testado");

		Method returnInvocationMethod = flow.getReturnInvocationMethod();
		if (returnInvocationMethod != null) {
			sb.append("assertEquals(%1$sEsperado, %1$sReal);", method.getName());
		} else if (!method.isVoid()) {
			sb.appendln("assertEquals(esperado, %sReal);", method.getName());
		}

		JavaClass classUnderTest = flow.getMethod().getJavaClass();
		for (Field f : flow.getWrittenFields()) {
			sb.append("assertEquals(expected, %s.%s());", classUnderTest.variableNameForType(),
					f.getGetter());
		}


		return sb.toString();
	}

	String generateInvokeMethod() {
		SourceBuilder sb = new SourceBuilder();
		sb.appendLineComment("invocar metodo sendo testado");
		String strParams = concatMethodParams(method.getFormalParameters());
		if (method.isVoid()) {
			sb.appendln("%s.%s(%s);",
					varNameForClassUnderTest,
					method.getName(),
					strParams);
		} else {
			sb.appendln("final %s %sReal = %s.%s(%s);",
					method.getType().getName(),
					method.getName(),
					varNameForClassUnderTest,
					method.getName(),
					strParams);

		}
		return sb.toString();
	}

	/**
	 * @param params
	 * @return
	 */
	private String concatMethodParams(List<FormalParameter> params) {
		String strParams = "";
		for (FormalParameter p : params) {
			if (!strParams.isEmpty())
				strParams += ",";
			strParams += p.getVariableId();
		}
		return strParams;
	}

	String generateConfigInternalState() {
		SourceBuilder sb = new SourceBuilder();
		if (classUnderTest.isAtView()) {
			sb.appendLineComment("Configurando estado interno da classe sob teste");
		}
		JavaClass classUnderTest = flow.getMethod().getJavaClass();
		for (Field f : flow.getReadFields()){
			if (classUnderTest.getFields().contains(f)) {
				sb.appendln("%s.%s(%s);", classUnderTest.variableNameForType(),
						f.getSetter(), f.getType().getNewValue());
			}
		}
		return sb.toString();
	}

	String generateStartMethod() {
		SourceBuilder sb = new SourceBuilder();
		sb.appendln("@Test");
		sb.appendln("public void test%s%s() {",
				upperCaseFirstChar(method.getName()),
				method.getFlows().size() > 1 ? flow.getName() : "");
		return sb.toString();
	}

	String generateConfigMocks() {
		SourceBuilder sb = new SourceBuilder();
		sb.appendLineComment("Configurando mocks (" + usedDependencies.size() + ")");
		for(Field mock : usedDependencies) {
			if (usedDependencies.size() > 1) {
				sb.appendLineComment(mock.getName());
			}
			sb.appendln("%s %s = criarMock%s()", mock.getType(), mock.getName(), mock.getType());
			for (FieldMethodInvocation invocation : flow.getInvocations()) {
				if (!mock.equals(invocation.getInvokedAtField())) continue;

				final Type methodType = invocation.getMethod().getType();
				if (invocation.getMethod().isVoid()) {
					sb.appendln("%s.%s(%s);", mock.getName(),
							invocation.getMethod().getName(),
							invocation.getArgumentsAsString());
				} else {
					if (invocation.isReturnInvocation()) {
						sb.appendln("expect(%s.%s(%s))\n\t.andReturn(%sEsperado);", mock.getName(),
								invocation.getMethod().getName(),
								invocation.getArgumentsAsString(),
								lowerCaseFirstChar(invocation.getMethod().getName()));
					} else {
					    //TODO tratar a atribuicao feita
						sb.appendln("expect(%s.%s(%s))\n\t.andReturn(%s);", mock.getName(),
								invocation.getMethod().getName(),
								invocation.getArgumentsAsString(),
								methodType.getNewValue());
					}
				}
			}
			sb.append("replay(%s);", mock.getName());
			sb.appendln();
		}
		return sb.toString();


		// if (dependencies.isEmpty()) return;
		// sb.append( "\n// configurar mocks");
		// // String mock = selectByPassMethodMock();
		// // if (mock == null) return;
		//
		// String strParams = concatMethodParams(method.getParams());
		//
		// if (method.isVoid()) {
		// sb.appendln("%s.%s(%s);", mock, method.getName(), strParams));
		// } else {
		// sb.appendln("final %s esperado = %s;",
		// method.getType().getClassName(), newValueForType(method.getType())));
		// sb.appendln("expect(%s.%s(%s)).andReturn(esperado);", mock,
		// method.getName(), strParams));
		// }
		// sb.appendln("replay(%s);", mock));
		//
		// //List<String> mocks = guessMocksFromMethod();
	}

	String generateUsedVars() {
		SourceBuilder sb = new SourceBuilder();
		List<FormalParameter> params = method.getFormalParameters();
		Method returnInvocationMethod = flow.getReturnInvocationMethod();

		if (returnInvocationMethod != null || !params.isEmpty()) {
			sb.appendLineComment("variaveis usadas");
		}

		if (returnInvocationMethod != null) {
			sb.appendln("final %s %sEsperado = %s;", returnInvocationMethod.getType(),
					lowerCaseFirstChar(method.getName()), returnInvocationMethod.getType().getNewValue());
		}

		if (!params.isEmpty()) {

			for (FormalParameter fp : params) {
				sb.appendln("final %s %s = %s;", fp.getType().getName(), fp
						.getVariableId(), fp.getType().getNewValue());
			}
		}
		return sb.toString();
	}


	String generateCheckMocks() {
		SourceBuilder sb = new SourceBuilder();

		if (usedDependencies.isEmpty())
			return "";

		sb.appendLineComment("checar estados dos mocks");

		String mocks = "";
		for (Field dep : usedDependencies) {
			mocks += dep.getName() + ",";
		}
		sb.appendln("verify(" + mocks.substring(0, mocks.length() - 1) + ");");
		return sb.toString();
	}

}