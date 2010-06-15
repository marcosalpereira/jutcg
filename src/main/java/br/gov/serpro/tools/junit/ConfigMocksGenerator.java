package br.gov.serpro.tools.junit;

import static br.gov.serpro.tools.junit.GeneratorHelper.lowerCaseFirstChar;

import java.util.HashSet;
import java.util.Set;

import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.FieldMethodInvocation;
import br.gov.serpro.tools.junit.model.Flow;
import br.gov.serpro.tools.junit.model.Type;
import br.gov.serpro.tools.junit.model.Variable;

public class ConfigMocksGenerator {
	private Flow flow;
	private Set<Field> mocks;
	final SourceBuilder sb = new SourceBuilder();
	final Set<Variable> variablesDeclared = new HashSet<Variable>();

	public ConfigMocksGenerator(Flow flow, Set<Field> mocks) {
        this.flow = flow;
        this.mocks = mocks;
    }

    public String generate() {
        for(final Field mock : mocks) {
            if (mocks.size() > 1) {
                sb.appendLineComment(mock.getName());
            }
            sb.appendln("%s %s = criarMock%s();", mock.getType(), mock.getName(), mock.getType());
            for (final FieldMethodInvocation invocation : flow.getInvocations()) {
                if (!mock.equals(invocation.getInvokedAtField())) continue;

                if (invocation.isVoidInvocation()) {
                    configVoidInvocation(mock, invocation);
                } else {
                    if (invocation.isReturnInvocation()) {
                        configReturnInvocation(mock, invocation);
                    } else if(invocation.isAssignedInvocation()) {
                        configAssignedInvocation(mock, invocation);
                    } else {
                        configNonVoidInvocation(mock, invocation);
                    }
                }
            }
            sb.append("replay(%s);", mock.getName());
            sb.appendln();
        }
        if (!mocks.isEmpty()) {
            sb.insertLineComment(0, "Configurando mocks (" + mocks.size() + ")");
        }
        return sb.toString();

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
                methodType.getNewValue());
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
                    assignedVariable.getType().getNewValue());
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
