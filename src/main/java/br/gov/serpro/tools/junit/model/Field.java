package br.gov.serpro.tools.junit.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
	 * Initial value in the flow, if known.
	 */
	private String initialValueFlow;

	/**
	 * Value at the end of the flow, if known.
	 */
	private String endValueFlow;

	public void setProtection(final Protection protection) {
		this.protection = protection;
	}

	public Protection getProtection() {
		return protection;
	}

	public List<String> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(final List<String> annotations) {
		this.annotations = annotations;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	public String variableNameForType() {
		if (getType() == null) return null;
		return getType().getVariableName();
	}

	public boolean isStatic() {
		return ztatic;
	}

	public void setStatic(final boolean ztatic) {
		this.ztatic = ztatic;
	}

	public boolean isPrivate() {
		return protection == Protection.PRIVATE;
	}

	public String getInitialValueFlow() {
		return this.initialValueFlow;
	}

	public void setInitialValueFlow(final String initialValue) {
		this.initialValueFlow = initialValue;
	}

	public String getEndFlowValue() {
		return endValueFlow;
	}

	public void setEndFlowValue(final String endFlowValue) {
		this.endValueFlow = endFlowValue;
	}

	public boolean isInitialValueFlowKnown() {
		return this.initialValueFlow != null;
	}

	public boolean isEndValueFlowKnown() {
		return endValueFlow != null;
	}

	public boolean isEndFlowValueNullLiteral() {
		return getEndFlowValue().equals("null");
	}

	public boolean isEndFlowValueBooleanLiteral() {
		return isEndFlowValueTrueLiteral()
			|| isEndFlowValueFalseLiteral();
	}

	private boolean isEndFlowValueFalseLiteral() {
		return getEndFlowValue().equals("false");
	}

	public boolean isEndFlowValueTrueLiteral() {
		return getEndFlowValue().equals("true");
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
    public int compareTo(final Field o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Field)) {
			return false;
		}
		final Field o = (Field) obj;
		return new EqualsBuilder().
			append(getName(), o.getName()).
			isEquals();
    }

    @Override
    public int hashCode() {
    	return new HashCodeBuilder(321, 543).append(name).toHashCode();
    }
}
