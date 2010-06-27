package br.gov.serpro.tools.junit.generate;

import static br.gov.serpro.tools.junit.util.GeneratorHelper.lowerCaseFirstChar;

import java.util.HashSet;
import java.util.Set;

import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.FieldMethodInvocation;
import br.gov.serpro.tools.junit.model.Flow;
import br.gov.serpro.tools.junit.model.Type;
import br.gov.serpro.tools.junit.model.Variable;
import br.gov.serpro.tools.junit.util.SourceBuilder;

public class ConfigMocksGenerator {
	private final Flow flow;
	private final Set<Field> mocks;
	final SourceBuilder sb = new SourceBuilder();
	final Set<Variable> variablesDeclared = new HashSet<Variable>();
	final private NextValueForType nextValueForType;

	public ConfigMocksGenerator(Flow flow, Set<Field> mocks, NextValueForType nextValueForType) {
        this.flow = flow;
        this.mocks = mocks;
        this.nextValueForType = nextValueForType;
    }

    public String generate() {

    	if (mocks.size() > 0) {
    		sb.appendln();
    		if (mocks.size() == 1) {
    			sb.appendln("// Configurando mock");
    		} else  {
    			sb.appendln("// Configurando mocks (" + mocks.size() + ")");
    		}
        }

        for(final Field mock : mocks) {
            if (mocks.size() > 1) {
                sb.appendln("// " + mock.getName());
            }
            sb.appendln("%s %s = criarMock%s();", mock.getType(), mock.getName(), mock.getType());
            for (final FieldMethodInvocation invocation : flow.getInvocations()) {
                if (!mock.equals(invocation.getInvokedAtField())) continue;

                if (invocation.isVoidInvocation()) {
                    configVoidInvocation(mock, invocation);
                } else {
                    if (invocation.isReturnInvocation()) {
                        configReturnInvocation(mock, invocation);
                    } else if (invocation.isAssignedInvocation()) {
						configAssignedInvocation(mock, invocation);
                    } else if (invocation.isReturnedValueKnown()) {
                    	configKnowReturnedValueInvocation(mock, invocation);
					} else {
                        configNonVoidInvocation(mock, invocation);
                    }
                }
            }
            sb.appendln("replay(%s);", mock.getName());
        }
        return sb.toString();

	}

    private void configKnowReturnedValueInvocation(Field mock,
			FieldMethodInvocation invocation) {
		sb.appendln("expect(%s.%s(%s))\n  .andReturn(%s);", mock.getName(),
				invocation.getMethod().getName(), invocation
						.getArgumentsAsString(), invocation.getReturnedValue());
	}

	/**
     * @param mock
     * @param invocation
     */
    private void configNonVoidInvocation(final Field mock,
        final FieldMethodInvocation invocation) {
        final Type methodType = invocation.getMethod().getType();
        sb.appendln("expect(%s.%s(%s))\n  .andReturn(%s);", mock.getName(),
                invocation.getMethod().getName(),
                invocation.getArgumentsAsString(),
                nextValueForType.next(methodType));
    }

    /**
     * @param mock
     * @param invocation
     */
    private void configAssignedInvocation(final Field mock,
        final FieldMethodInvocation invocation) {
        final Variable assignedVariable = invocation
                .getAssignedVariable();
        if (assignedVariable.isScopeLocal()
                && !variablesDeclared.contains(assignedVariable)) {
            sb.appendln("%s %s = %s;",
                    assignedVariable.getType().getName(),
                    assignedVariable.getName(),
                    nextValueForType.next(assignedVariable.getType()));
            variablesDeclared.add(assignedVariable);
        }
        sb.appendln("expect(%s.%s(%s))\n  .andReturn(%s);", mock.getName(),
                invocation.getMethod().getName(),
                invocation.getArgumentsAsString(),
                assignedVariable.getName());
    }

    /**
     * @param mock
     * @param invocation
     */
    private void configReturnInvocation(final Field mock,
        final FieldMethodInvocation invocation) {
        sb.appendln("expect(%s.%s(%s))\n  .andReturn(%sFromMock);", mock.getName(),
                invocation.getMethod().getName(),
                invocation.getArgumentsAsString(),
                lowerCaseFirstChar(invocation.getMethod().getName()));
    }

    /**
     * @param mock
     * @param invocation
     */
    private void configVoidInvocation(final Field mock,
        final FieldMethodInvocation invocation) {
        sb.appendln("%s.%s(%s);", mock.getName(),
                invocation.getMethod().getName(),
                invocation.getArgumentsAsString());
    }

}
