package br.gov.serpro.tools.junit.model;

import java.util.List;

/**
 * Formal parameters of a method.
 */
public class FormalParameter {
    /**
     * Formal parameter name.
     */
    private String variableId;

    /**
     * Field type.
     */
    private Type type;

    /**
     * Field's annotations.
     */
    private List<Type> annotations;

    public List<Type> getAnnotations() {
        return this.annotations;
    }

    public void setAnnotations(final List<Type> annotations) {
        this.annotations = annotations;
    }

    public String getVariableId() {
        return this.variableId;
    }

    public void setVariableId(final String name) {
        this.variableId = name;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

}
