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
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public boolean isScopeLocal() {
		return Scope.LOCAL_SCOPE.equals(getScope());
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isValueKnown() {
		return this.value != null;
	}
}
