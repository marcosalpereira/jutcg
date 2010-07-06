package br.gov.serpro.tools.junit.generate;

import static br.gov.serpro.tools.junit.util.GeneratorHelper.lowerCaseFirstChar;

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
import br.gov.serpro.tools.junit.util.SourceBuilder;

public class TestCaseMethodFlowGenerator {
	final private Method method;
	final private Flow flow;
	final private JavaClass classUnderTest;
	final private String varNameForClassUnderTest;
	final private Set<Field> usedDependencies;
	final private NextValueForType nextValueForType;

	public TestCaseMethodFlowGenerator(Flow flow, List<Field> dependencies) {
		this.flow = flow;
		this.method = flow.getMethod();
		this.classUnderTest = method.getJavaClass();
		this.varNameForClassUnderTest = classUnderTest.variableNameForType();
		this.usedDependencies = selectUsedDependencies(dependencies);
		this.nextValueForType = new NextValueForType();
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
		sb.appendln();

		if (!flow.getFlowBranches().isEmpty()) {
			sb.appendJavaDoc("Teste para o metodo {@link %s#%s}."
			    + "\nDescricao do Fluxo: %s.",
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

		sb.append(generateStartMethod());
		sb.append(usedVars);
		sb.append(configMocks);
		sb.append(configInternalState);
		sb.append(invokeMethod);
		sb.append(checkMocks);
		sb.append(verifys);
		sb.append(generateEndMethod());

		return sb.toString();
	}

	private String generateFlowDescription(List<FlowBranch> flowBranches) {
	    final StringBuilder sb = new StringBuilder();
        for (final FlowBranch flowBranch : flowBranches) {
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
		return "}\n";
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

		for (final Field f : flow.getWrittenFields()) {
			if(f.isWrittenValueKnown()) {
				appendAssertionKnownWrittenValue(sb, f);
			} else {
				sb.appendln("final %s %sExpected = %s;", f.getType(), f.getGetter(), "<IDontKnowButYouDo>");
				sb.appendln("assertEquals(%sExpected, %s.%s());",
						f.getGetter(),
						varNameForClassUnderTest,
						f.getGetter());
			}
			infoAppended = true;
		}

		if (infoAppended) {
		    sb.insertln(0, "\n// verificacoes do resultado do metodo sendo testado");
		}

		return sb.toString();
	}

	private void appendAssertionKnownWrittenValue(final SourceBuilder sb,
			final Field f) {
		if(f.isWrittenValueNullLiteral()) {
			sb.appendln("assertNull(%s.%s());",
					varNameForClassUnderTest,
					f.getGetter());
		} else {
			sb.appendln("assertEquals(%s, %s.%s());",
					f.getWrittenValue(),
					varNameForClassUnderTest,
					f.getGetter());
		}
	}

	String generateInvokeMethod() {
		final SourceBuilder sb = new SourceBuilder();
		sb.appendln();
		sb.appendln("// invocar metodo sendo testado");
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

		final List<Field> fields = flow.selectNonStaticReadFields();
		fields.removeAll(usedDependencies);

		if (fields.isEmpty()) return "";

		final SourceBuilder sb = new SourceBuilder();
		sb.appendln("\n// Configurando estado interno da classe sob teste");

		for (final Field f : fields){
			sb.appendln("%s.%s(%s);", varNameForClassUnderTest, f.getSetter(), f.getName());
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
	    return new ConfigMocksGenerator(flow, usedDependencies, nextValueForType)
	       .generate();
	}

	String generateUsedVars() {
		final SourceBuilder sb = new SourceBuilder();
		final List<FormalParameter> params = method.getFormalParameters();
		final Method returnInvocationMethod = flow.getReturnInvocationMethod();
		final List<Field> readFields = flow.selectNonStaticReadFields();
		readFields.removeAll(usedDependencies);

		if (returnInvocationMethod != null || !params.isEmpty() || !readFields.isEmpty()) {
			sb.appendln("// variaveis usadas");
		}

		//Declare readFields/written as local vars
		for (final Field f : readFields){
			sb.appendln("final %s %s = %s;", f.getType(), f.getName(),
					nextValueForType.next(f.getType()));
        }

		if (returnInvocationMethod != null) {
			String nextValue = nextValueForType.next(returnInvocationMethod.getType());
			sb.appendln("final %s %sFromMock = %s;", returnInvocationMethod.getType(),
					lowerCaseFirstChar(returnInvocationMethod.getName()),
					nextValue);
			sb.appendln("final %s %sEsperado = %s;", returnInvocationMethod.getType(),
					lowerCaseFirstChar(returnInvocationMethod.getName()),
					nextValue);
		}

		if (!params.isEmpty()) {

			for (final FormalParameter fp : params) {
				sb.appendln("final %s %s = %s;", fp.getType().getName(), fp
						.getVariableId(), nextValueForType.next(fp.getType()));
			}
		}
		return sb.toString();
	}


	String generateCheckMocks() {
		if (usedDependencies.isEmpty()) {
			return "";
		}

		final SourceBuilder sb = new SourceBuilder();

		String mocks = "";
		for (final Field dep : usedDependencies) {
			mocks += dep.getName() + ",";
		}

		sb.appendln();
		sb.appendln("// checar estados dos mocks");
		sb.appendln("verify(" + mocks.substring(0, mocks.length() - 1) + ");");
		return sb.toString();
	}

}
