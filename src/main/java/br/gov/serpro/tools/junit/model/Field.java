package br.gov.serpro.tools.junit.model;

import java.util.ArrayList;
import java.util.List;

import br.gov.serpro.tools.junit.util.GeneratorHelper;


public class Field implements Comparable<Field>{
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

	/**
	 * Assigned value, if known.
	 */
	private String writtenValue;

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

	public String getWrittenValue() {
		return writtenValue;
	}

	public void setWrittenValue(String writtenValue) {
		this.writtenValue = writtenValue;
	}

	public boolean isWrittenValueKnown() {
		return writtenValue != null;
	}

	public boolean isWrittenValueNullLiteral() {
		return getWrittenValue().equals("null");
	}

	public boolean isWrittenValueBooleanLiteral() {
		return isWrittenValueTrueLiteral()
			|| isWrittenValueFalseLiteral();
	}

	private boolean isWrittenValueFalseLiteral() {
		return getWrittenValue().equals("false");
	}

	public boolean isWrittenValueTrueLiteral() {
		return getWrittenValue().equals("true");
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

    @Override
    public int compareTo(Field o) {
        return getName().compareTo(o.getName());
    }
}
