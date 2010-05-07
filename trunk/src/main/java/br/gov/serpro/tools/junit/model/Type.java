package br.gov.serpro.tools.junit.model;

public class Type {
	private String name;

	private String variableName;

	private boolean primitive;

	private String fullName;

	public String getNewValue() {
		if (!isPrimitive()) {
			if (isCollection()) {
				return String.format("new %s()", getCollectionImpl());
			}
			return String.format("new %s(1)", getName());
		}

		if (getName().equals("boolean")) {
			return "false";
		}

		return "0";
	}

	private String getCollectionImpl() {
		if (isSet()) return "Hash" + name;
		if (isList()) return "Array" + name;
		return name;
	}

	private boolean isCollection() {
		return isList() || isSet();
	}

	private boolean isSet() {
		return name.startsWith("Set");
	}

	private boolean isList() {
		return name.startsWith("List");
		}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.variableName = defineVariableName();
	}

	public void setPrimitive(boolean primitive) {
		this.primitive = primitive;
	}

	public boolean isPrimitive() {
		return primitive;
	}

	public String getVariableName() {
		return variableName;
	}

	private String defineVariableName() {

		if (name == null) {
			return null;
		} else {
			return name.substring(0, 1).toLowerCase() + name.substring(1);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return name;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}



}
