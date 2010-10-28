package br.gov.serpro.tools.junit.model;

/**
 * Represents variable.
 */
public class Variable {

    private Type type;

    private String name;

    private Scope scope;

    private String value;

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Scope getScope() {
        return this.scope;
    }

    public void setScope(final Scope scope) {
        this.scope = scope;
    }

    public boolean isScopeLocal() {
        return Scope.LOCAL_SCOPE.equals(getScope());
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public boolean isValueKnown() {
        return this.value != null;
    }
}
