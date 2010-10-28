package br.gov.serpro.tools.junit.generate;

import static br.gov.serpro.tools.junit.util.GeneratorHelper.lowerCaseFirstChar;

import java.util.HashSet;
import java.util.Set;

import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.FieldMethodInvocation;
import br.gov.serpro.tools.junit.model.Flow;
import br.gov.serpro.tools.junit.model.Type;
import br.gov.serpro.tools.junit.model.Variable;

/**
 * Config mocks of flow.
 */
public class ConfigMocksGenerator {
    /**
     * Flow.
     */
    private final Flow flow;
    /**
     * Mocks.
     */
    private final Set<Field> mocks;
    /**
     * Control variable declarations.
     */
    private final Set<Variable> variablesDeclared = new HashSet<Variable>();
    /**
     * Control values for types.
     */
    private final NextValueForType nextValueForType;
    /**
     * Config mock section.
     */
    private final ConfigMocks configMocks;

    /**
     * Constructor.
     * @param testMethod test method
     * @param flow flow
     * @param mocks mocks
     * @param nextValueForType instance of control values for types
     */
    public ConfigMocksGenerator(final TestMethod testMethod, final Flow flow,
            final Set<Field> mocks, final NextValueForType nextValueForType) {
        this.flow = flow;
        this.mocks = mocks;
        this.nextValueForType = nextValueForType;
        this.configMocks = testMethod.getConfigMocks();
    }

    /**
     * Generate.
     */
    public final void generate() {
        if (this.mocks.size() > 0) {
            if (this.mocks.size() == 1) {
                this.configMocks.setDescription("Configurando mock");
            } else {
                this.configMocks.setDescription("Configurando mocks ("
                        + this.mocks.size() + ")");
            }
        }

        for (final Field mock : this.mocks) {
            if (this.mocks.size() > 1) {
                this.configMocks.addCode("// " + mock.getName());
            }
            this.configMocks.addCode("%s %s = criarMock%s();", mock.getType(),
                    mock.getName(), mock.getType());
            for (final FieldMethodInvocation invocation : this.flow
                    .getInvocations()) {
                if (!mock.equals(invocation.getInvokedAtField())) continue;

                if (invocation.isVoidInvocation()) {
                    configVoidInvocation(mock, invocation);
                } else {
                    if (invocation.isReturnInvocation()) {
                        configReturnInvocation(mock, invocation);
                    } else if (invocation.isAssignedInvocation()) {
                        configAssignedInvocation(mock, invocation);
                    } else if (invocation.isReturnedValueKnown()) {
                        configKnownReturnedValueInvocation(mock, invocation);
                    } else {
                        configNonVoidInvocation(mock, invocation);
                    }
                }
            }
            this.configMocks.addCode("replay(%s);", mock.getName());
        }

    }

    /**
     * Config known returned value invocation.
     * @param mock mock
     * @param invocation invocation
     */
    private void configKnownReturnedValueInvocation(final Field mock,
            final FieldMethodInvocation invocation) {
        this.configMocks.addCode("expect(%s.%s(%s))\n  .andReturn(%s);", mock
                .getName(), invocation.getMethod().getName(), invocation
                .getArgumentsAsString(), invocation.getReturnedValue());
    }

    /**
     * @param mock
     * @param invocation
     */
    private void configNonVoidInvocation(final Field mock,
            final FieldMethodInvocation invocation) {
        final Type methodType = invocation.getMethod().getType();
        this.configMocks
                .addCode("expect(%s.%s(%s))\n  .andReturn(%s);",
                        mock.getName(), invocation.getMethod().getName(),
                        invocation.getArgumentsAsString(),
                        this.nextValueForType.next(methodType));
    }

    /**
     * @param mock
     * @param invocation
     */
    private void configAssignedInvocation(final Field mock,
            final FieldMethodInvocation invocation) {
        final Variable assignedVariable = invocation.getAssignedVariable();
        if (assignedVariable.isScopeLocal()
                && !this.variablesDeclared.contains(assignedVariable)) {
            this.configMocks.addCode("%s %s = %s;", assignedVariable.getType()
                    .getName(), assignedVariable.getName(),
                    getAssignedVariableValue(assignedVariable));
            this.variablesDeclared.add(assignedVariable);
        }
        this.configMocks.addCode("expect(%s.%s(%s))\n  .andReturn(%s);", mock
                .getName(), invocation.getMethod().getName(), invocation
                .getArgumentsAsString(), assignedVariable.getName());
    }

    private String getAssignedVariableValue(final Variable assignedVariable) {
        if (assignedVariable.isValueKnown()) {
            return assignedVariable.getValue();
        }
        return this.nextValueForType.next(assignedVariable.getType());
    }

    /**
     * @param mock
     * @param invocation
     */
    private void configReturnInvocation(final Field mock,
            final FieldMethodInvocation invocation) {
        this.configMocks.addCode(
                "expect(%s.%s(%s))\n  .andReturn(%sFromMock);", mock.getName(),
                invocation.getMethod().getName(), invocation
                        .getArgumentsAsString(), lowerCaseFirstChar(invocation
                        .getMethod().getName()));
    }

    /**
     * @param mock
     * @param invocation
     */
    private void configVoidInvocation(final Field mock,
            final FieldMethodInvocation invocation) {
        this.configMocks.addCode("%s.%s(%s);", mock.getName(), invocation
                .getMethod().getName(), invocation.getArgumentsAsString());
    }

}
