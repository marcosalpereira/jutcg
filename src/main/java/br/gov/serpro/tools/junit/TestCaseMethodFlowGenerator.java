package br.gov.serpro.tools.junit;

import static br.gov.serpro.tools.junit.GeneratorHelper.lowerCaseFirstChar;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.FieldMethodInvocation;
import br.gov.serpro.tools.junit.model.Flow;
import br.gov.serpro.tools.junit.model.FormalParameter;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.model.Method;
import br.gov.serpro.tools.junit.model.Flow.FlowBranch;

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
		final Set<Field> ret = new HashSet<Field>();
		for (final FieldMethodInvocation invocation : flow.getInvocations()) {
			if (dependencies.contains(invocation.getInvokedAtField())) {
				ret.add(invocation.getInvokedAtField());
			}
		}
		return ret;
	}

	public String generate() {
		final SourceBuilder sb = new SourceBuilder();

		if (!flow.getFlowBranches().isEmpty()) {

			sb.appendJavaDoc("Teste para o metodo {@link %s#%s}.\nDescricao do Fluxo: %s.",
				method.getJavaClass().getType(),
				method.getLoggingSignature(),
				generateFlowDescription(flow.getFlowBranches()));
		} else {
			sb.appendJavaDoc("Teste para o metodo {@link %s#%s}.",
					method.getJavaClass().getType(), method.getLoggingSignature());
		}

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

	private String generateFlowDescription(List<FlowBranch> flowBranches) {
	    StringBuilder sb = new StringBuilder();
        for (FlowBranch flowBranch : flowBranches) {
            if (flowBranch.isEnter()) {
                sb.append("\n    Entra: ");
            } else {
                sb.append("\nNAO Entra: ");
            }
            sb.append(flowBranch.getExpression());
        }

        return sb.toString();
    }

    String generateEndMethod() {
		return "}";
	}

	String generateVerifys() {
		final SourceBuilder sb = new SourceBuilder();
		boolean infoAppended = false;

		final Method returnInvocationMethod = flow.getReturnInvocationMethod();
		if (returnInvocationMethod != null) {
			sb.appendln("assertEquals(%sEsperado, %sReal);", returnInvocationMethod.getName(),
					method.getName());
			infoAppended = true;
		} else if (!method.isVoid()) {
			sb.appendln("assertEquals(esperado, %sReal);", method.getName());
			infoAppended = true;
		}

		final JavaClass classUnderTest = flow.getMethod().getJavaClass();
		for (final Field f : flow.getWrittenFields()) {
			sb.appendln("assertEquals(expected, %s.%s());", classUnderTest.variableNameForType(),
					f.getGetter());
			infoAppended = true;
		}

		if (infoAppended) {
		    sb.insertLineComment(0, "verificacoes do resultado do metodo sendo testado");
		}

		return sb.toString();
	}

	String generateInvokeMethod() {
		final SourceBuilder sb = new SourceBuilder();
		sb.appendLineComment("invocar metodo sendo testado");
		final String strParams = concatMethodParams(method.getFormalParameters());
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
		for (final FormalParameter p : params) {
			if (!strParams.isEmpty())
				strParams += ",";
			strParams += p.getVariableId();
		}
		return strParams;
	}

	String generateConfigInternalState() {
		final SourceBuilder sb = new SourceBuilder();

		final JavaClass classUnderTest = flow.getMethod().getJavaClass();

		if (!flow.getReadFields().isEmpty()) {
			sb.appendLineComment("Configurando estado interno da classe sob teste");
		}
		for (final Field f : flow.getReadFields()){
			if (classUnderTest.getFields().contains(f)) {
				sb.appendln("%s.%s(%s);", classUnderTest.variableNameForType(),
						f.getSetter(), f.getType().getNewValue());
			}
		}
		return sb.toString();
	}

	String generateStartMethod() {
		final SourceBuilder sb = new SourceBuilder();
		sb.appendln("@Test");
		sb.appendln("public void %s() {", method.getNameForTest(flow));
		return sb.toString();
	}

	String generateConfigMocks() {
	    return new ConfigMocksGenerator(flow, usedDependencies)
	       .generate();
	}

	String generateUsedVars() {
		final SourceBuilder sb = new SourceBuilder();
		final List<FormalParameter> params = method.getFormalParameters();
		final Method returnInvocationMethod = flow.getReturnInvocationMethod();

		if (returnInvocationMethod != null || !params.isEmpty()) {
			sb.appendLineComment("variaveis usadas");
		}

		if (returnInvocationMethod != null) {
			sb.appendln("final %s %sEsperado = %s;", returnInvocationMethod.getType(),
					lowerCaseFirstChar(returnInvocationMethod.getName()),
					returnInvocationMethod.getType().getNewValue());
		}

		if (!params.isEmpty()) {

			for (final FormalParameter fp : params) {
				sb.appendln("final %s %s = %s;", fp.getType().getName(), fp
						.getVariableId(), fp.getType().getNewValue());
			}
		}
		return sb.toString();
	}


	String generateCheckMocks() {
		final SourceBuilder sb = new SourceBuilder();

		String mocks = "";
		for (final Field dep : usedDependencies) {
			mocks += dep.getName() + ",";
		}

		if (!usedDependencies.isEmpty()) {
		    sb.appendLineComment("checar estados dos mocks");
		    sb.appendln("verify(" + mocks.substring(0, mocks.length() - 1) + ");");
		}
		return sb.toString();
	}

}
