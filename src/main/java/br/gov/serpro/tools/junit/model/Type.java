package br.gov.serpro.tools.junit.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public class Type {
	private String name;

	private String variableName;

	private boolean primitive;

	private String fullName;

	private Type[] generic; 

	public Type[] getGeneric() {
		if (generic != null) return generic;
		
		int iniGen = this.name.indexOf('<');
		int lastIniGen = this.name.lastIndexOf('<');

		//if a complex generic, like List<List<?>>
		//we will ignore
		if (iniGen != lastIniGen) return null;

		if (iniGen > 0) {
			int endGen = this.name.indexOf('>');
			String sNomes = this.name.substring(iniGen+1, endGen);
			String[] aNomes = sNomes.split(",");
			generic = new Type[aNomes.length];
			for(int i=0; i<aNomes.length; i++) {
				final String nome = aNomes[i].trim();
				generic[i] = new Type();
				generic[i].setName(nome);
				generic[i].setFullName(nome);
				generic[i].setPrimitive(Character.isLowerCase(nome.charAt(0)));
			}
		}

		return generic;
	}

	/**
	 * note: map is considered a collection too.
	 * @return <code>true</code> if is a List, Set or Map
	 */
	public boolean isCollection() {
		return isList() || isSet() || isMap();
	}

	public String getDefaultCollectionImpl() {
		if (isMap()) return "HashMap";
		if (isSet()) return "HashSet";
		if (isList()) return "ArrayList";
		return null;
	}

	public boolean isSet() {
		return name.startsWith("Set");
	}
	
	public boolean isMap() {
		return name.startsWith("Map");
	}

	public boolean isList() {
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
		if (fullName == null) fullName = name;
		return fullName;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Type) {
			Type that = (Type) obj;
			return new EqualsBuilder()
				.append(this.getFullName(), that.getFullName())
				.isEquals();
		}

		return false;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(795681801, 961628501).append(
				this.getFullName()).toHashCode();
	}

}
