package br.gov.serpro.tools.junit.generate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.FieldMethodInvocation;
import br.gov.serpro.tools.junit.model.Flow;
import br.gov.serpro.tools.junit.model.Flow.FlowBranch;
import br.gov.serpro.tools.junit.model.FormalParameter;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.model.Method;

/**
 * Generates a flow of a method.
 */
public class TestCaseMethodFlowGenerator {
    private final Method method;
    private final Flow flow;
    private final JavaClass classUnderTest;
    private final String varNameForClassUnderTest;
    private final Set<Field> usedDependencies;
    private final NextValueForType nextValueForType;
    private final TestMethod testMethod;

    public TestCaseMethodFlowGenerator(final JunitTestCase testCase,
            final Flow flow, final List<Field> dependencies) {
        this.flow = flow;
        this.method = flow.getMethod();
        this.classUnderTest = this.method.getJavaClass();
        this.varNameForClassUnderTest = this.classUnderTest
                .variableNameForType();
        this.usedDependencies = selectUsedDependencies(dependencies);
        this.nextValueForType = new NextValueForType();
        this.testMethod = new TestMethod();
    }

    public TestMethod generate() {
        generateJavadoc();
        generateName();
        generateAnnotations();
        generateUsedVars();
        generateConfigMocks();
        generateConfigInternalState();
        generateInvokeMethod();
        generateCheckMocks();
        generateVerifys();
        return this.testMethod;
    }

    private void generateJavadoc() {
        if (!this.flow.getFlowBranches().isEmpty()) {
            this.testMethod.setJavaDoc("Teste para o metodo {@link %s#%s}."
                    + "\nDescricao do Fluxo: %s.", this.method.getJavaClass()
                    .getType(), this.method.getLoggingSignature(),
                    generateFlowDescription(this.flow.getFlowBranches()));
        } else {
            this.testMethod.setJavaDoc("Teste para o metodo {@link %s#%s}.",
                    this.method.getJavaClass().getType(), this.method
                            .getLoggingSignature());
        }
    }

    private void appendAssertionKnownWrittenValue(final Field f) {
        if (f.isEndFlowValueNullLiteral()) {
            this.testMethod.getAsserts().addCode("assertNull(%s.%s());",
                    this.varNameForClassUnderTest, f.getGetter());
        } else if (f.isEndFlowValueBooleanLiteral()) {
            if (f.isEndFlowValueTrueLiteral()) {
                this.testMethod.getAsserts().addCode("assertTrue(%s.%s());",
                        this.varNameForClassUnderTest, f.getGetter());
            } else {
                this.testMethod.getAsserts().addCode("assertFalse(%s.%s());",
                        this.varNameForClassUnderTest, f.getGetter());
            }
        } else {
            this.testMethod.getAsserts().addCode("assertEquals(%s, %s.%s());",
                    f.getEndFlowValue(), this.varNameForClassUnderTest,
                    f.getGetter());
        }
    }

    /**
     * @param params
     * @return
     */
    private String concatMethodParams(final List<FormalParameter> params) {
        String strParams = "";
        for (final FormalParameter p : params) {
            if (!strParams.isEmpty()) {
	            strParams += ",";
            }
            strParams += p.getVariableId();
        }
        return strParams;
    }

    private String generateFlowDescription(final List<FlowBranch> flowBranches) {
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

    private String getInitialValue(final Field field) {
        String initialValue = null;
        if (field.isInitialValueFlowKnown()) {
            initialValue = field.getInitialValueFlow();
        } else {
            initialValue = this.nextValueForType.next(field.getType());
        }
        return initialValue;
    }

    private Set<Field> selectUsedDependencies(final List<Field> dependencies) {
        final Set<Field> ret = new HashSet<Field>();
        for (final FieldMethodInvocation invocation : this.flow
                .getInvocations()) {
            if (dependencies.contains(invocation.getInvokedAtField())) {
                ret.add(invocation.getInvokedAtField());
            }
        }
        return ret;
    }

    private void generateCheckMocks() {
        if (this.usedDependencies.isEmpty()) {
            return;
        }

        String mocks = "";
        for (final Field dep : this.usedDependencies) {
            mocks += dep.getName() + ",";
        }

        this.testMethod.getVerifys().addCode(
                "verify(" + mocks.substring(0, mocks.length() - 1) + ");");
    }

    private void generateConfigInternalState() {

        final List<Field> fields = this.flow.selectNonStaticReadFields();
        fields.removeAll(this.usedDependencies);

        if (fields.isEmpty()) {
	        return;
        }

        for (final Field f : fields) {
            this.testMethod.getConfigInternalState().addCode("%s.%s(%s);",
                    this.varNameForClassUnderTest, f.getSetter(), f.getName());
        }
    }

    private void generateConfigMocks() {
        new ConfigMocksGenerator(this.testMethod, this.flow,
                this.usedDependencies, this.nextValueForType).generate();
    }

    private void generateInvokeMethod() {
        final String strParams = concatMethodParams(this.method
                .getFormalParameters());
        if (this.method.isVoid()) {
            this.testMethod.getInvokeMethod().addCode("%s.%s(%s);",
                    this.varNameForClassUnderTest, this.method.getName(),
                    strParams);
        } else {
            this.testMethod.getInvokeMethod().addCode(
                    "final %s %s = %s.%s(%s);",
                    this.method.getType().getName(), this.method.getVarNameForRealContentInvocation(),
                    this.varNameForClassUnderTest, this.method.getName(),
                    strParams);

        }
    }

    private void generateName() {
        this.testMethod.setName(this.method.getNameForTest(this.flow));
    }

    private void generateAnnotations() {
        this.testMethod.getAnnotations().add("Test");
    }

    private void generateUsedVars() {
        final List<FormalParameter> params = this.method.getFormalParameters();
        final Method returnInvocationMethod = this.flow
                .getReturnInvocationMethod();
        final List<Field> readFields = this.flow.selectNonStaticReadFields();
        readFields.removeAll(this.usedDependencies);

        // Declare readFields/written as local vars
        for (final Field f : readFields) {
            this.testMethod.getUsedVars().addCode("final %s %s = %s;",
                    f.getType(), f.getName(), getInitialValue(f));
        }

        if (returnInvocationMethod != null) {
            final String nextValue = this.nextValueForType
                    .next(returnInvocationMethod.getType());
            final String varNameFromMock =
            		returnInvocationMethod.getVarNameFromReturningMock();
            this.testMethod.getUsedVars().addCode("final %s %s = %s;",
                    returnInvocationMethod.getType(),
                    varNameFromMock,
                    nextValue);
        }

        if (!params.isEmpty()) {
            for (final FormalParameter fp : params) {
                this.testMethod.getUsedVars().addCode("final %s %s = %s;",
                        fp.getType().getName(), fp.getVariableId(),
                        this.nextValueForType.next(fp.getType()));
            }
        }
    }

	private void generateVerifys() {
        final Method returnInvocationMethod = this.flow
                .getReturnInvocationMethod();
        if (returnInvocationMethod != null) {
        	final String varNameForRealContentInvocation =
        			returnInvocationMethod.getVarNameForRealContentInvocation();
            final String varNameFromMock =
            		returnInvocationMethod.getVarNameFromReturningMock();
			this.testMethod.getAsserts().addCode(
                    "assertEquals(%s, %s);",
                    varNameFromMock, varNameForRealContentInvocation);
        } else if (!this.method.isVoid()) {
        	final String varNameForRealContentInvocation =
        			this.method.getVarNameForRealContentInvocation();
            this.testMethod.getAsserts().addCode(
                    "assertEquals(esperado, %s);", this.method.getName(),
                    varNameForRealContentInvocation);
        }

        for (final Field f : this.flow.getWrittenFields()) {
            if (f.isEndValueFlowKnown()) {
                appendAssertionKnownWrittenValue(f);
            } else {
                this.testMethod.getAsserts().addCode(
                        "final %s %sExpected;", f.getType(),
                        f.getGetter());
                this.testMethod.getAsserts().addCode(
                        "assertEquals(%sExpected, %s.%s());", f.getGetter(),
                        this.varNameForClassUnderTest, f.getGetter());
            }
        }

    }

}
