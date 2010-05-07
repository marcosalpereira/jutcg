package br.gov.serpro.tools.junit.model;

import java.util.ArrayList;
import java.util.List;

import br.gov.serpro.tools.junit.GeneratorHelper;


public class Field {
	/**
	 * Field name.
	 */
	private String name;

	/**
	 * Field type.
	 */
	private Type type;

	/**
	 * Is field static
	 */
	private boolean ztatic;

	/**
	 * Field's annotations.
	 */
	private List<String> annotations = new ArrayList<String>();

	/**
	 * Field protection.
	 */
	private Protection protection;

	public void setProtection(Protection protection) {
		this.protection = protection;
	}

	public Protection getProtection() {
		return protection;
	}

	public List<String> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String variableNameForType() {
		if (getType() == null) return null;
		return getType().getVariableName();
	}

	public boolean isStatic() {
		return ztatic;
	}

	public void setStatic(boolean ztatic) {
		this.ztatic = ztatic;
	}

	public boolean isPrivate() {
		return protection == Protection.PRIVATE;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

	public String getSetter() {
		return "set" + GeneratorHelper.upperCaseFirstChar(name);
	}

	public String getGetter() {
		if ("boolean".equals(this.getType().getName())) {
			return "is" + GeneratorHelper.upperCaseFirstChar(name);
		}
		return "get" + GeneratorHelper.upperCaseFirstChar(name);
	}
}
