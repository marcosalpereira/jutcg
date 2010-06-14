package br.gov.serpro.tools.junit.model;


public class Type {
	private String name;

	private String variableName;

	private boolean primitive;

	private String fullName;

	public String getNewValue() {
		if (!isPrimitive()) {
			if (isCollection()) {
				return collectionInstance();
			} else if (getName().equals("String")) {
			    return "\"1\"";
			}
			return String.format("new %s(1)", getName());
		}

		if (getName().equals("boolean")) {
			return "false";
		}

		return "0";
	}

	private String collectionInstance() {
		if (isSet()) return "new Hash" + name + "()";
		if (isList()) {
			Type generic = getGeneric();
			if (generic != null) {
				return "Arrays.asList(" + generic.getNewValue() + ")";
			} else {
				return "new Array" + name + "()";
			}
		}
		return name;
	}

	private Type getGeneric() {
		Type ret = null;
		int iniGen = this.name.indexOf('<');
		int lastIniGen = this.name.lastIndexOf('<');

		//if a complex generic, like List<List<?>>
		//we will ignore
		if (iniGen != lastIniGen) return null;

		if (iniGen > 0) {
			//List<Cargo>
			int endGen = this.name.indexOf('>');
			String nome = this.name.substring(iniGen+1, endGen);
			ret = new Type();
			ret.setName(nome);
			ret.setFullName(nome);
			ret.setPrimitive(Character.isLowerCase(nome.charAt(0)));
		}

		return ret;
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
