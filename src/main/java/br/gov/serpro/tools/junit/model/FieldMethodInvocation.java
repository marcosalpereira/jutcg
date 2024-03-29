package br.gov.serpro.tools.junit.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Reprsents a method invocation on field class. field.method(args).
 */
public class FieldMethodInvocation {
    /**
     * Field that method was invoked in.
     */
    private Field invokedAtField;

    /**
     * Invoked method.
     */
    private Method method;

    /**
     * Arguments used on invocation.
     */
    private List<String> arguments = new ArrayList<String>();

    /**
     * Was this invocation used on <code>return</code> statment.
     */
    private boolean returnInvocation;

    /**
     * Invocation is assigned to a variable.
     */
    private Variable assignedVariable;

    /**
     * Result of the method invocation (if known).
     */
    private String returnedValue;

    public Field getInvokedAtField() {
        return this.invokedAtField;
    }

    public void setInvokedAtField(final Field field) {
        this.invokedAtField = field;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public List<String> getArguments() {
        return this.arguments;
    }

    public void setArguments(final List<String> arguments) {
        this.arguments = arguments;
    }

    public boolean isReturnInvocation() {
        return this.returnInvocation;
    }

    public void setReturnInvocation(final boolean returnInvocation) {
        this.returnInvocation = returnInvocation;
    }

    public Variable getAssignedVariable() {
        return this.assignedVariable;
    }

    public void setAssignedVariable(final Variable assignedVariable) {
        this.assignedVariable = assignedVariable;
    }

    public boolean isAssignedInvocation() {
        return getAssignedVariable() != null;
    }

    public String getReturnedValue() {
        return this.returnedValue;
    }

    public void setReturnedValue(final String returnedValue) {
        this.returnedValue = returnedValue;
    }

    public boolean isReturnedValueKnown() {
        return this.returnedValue != null;
    }

    public String getArgumentsAsString() {
        final StringBuilder result = new StringBuilder();
        final List<String> args = getArguments();
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) {
                result.append(',');
            }
            result.append(args.get(i));
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return this.method + "(" + this.getArgumentsAsString() + ")";
    }

    public boolean isVoidInvocation() {
        return getMethod().isVoid();
    }
}
