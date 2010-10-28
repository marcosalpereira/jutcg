package br.gov.serpro.tools.junit.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.gov.serpro.tools.junit.util.GeneratorHelper;

public class Field implements Comparable<Field> {
    /**
     * Field name.
     */
    private String name;

    /**
     * Field type.
     */
    private Type type;

    /**
     * Is field static.
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
        return new EqualsBuilder().append(getName(), o.getName()).isEquals();
    }

    public List<String> getAnnotations() {
        return this.annotations;
    }

    public String getEndFlowValue() {
        return this.endValueFlow;
    }

    public String getGetter() {
        if ("boolean".equals(this.getType().getName())) {
            return "is" + GeneratorHelper.upperCaseFirstChar(this.name);
        }
        return "get" + GeneratorHelper.upperCaseFirstChar(this.name);
    }

    public String getInitialValueFlow() {
        return this.initialValueFlow;
    }

    public String getName() {
        return this.name;
    }

    public Protection getProtection() {
        return this.protection;
    }

    public String getSetter() {
        return "set" + GeneratorHelper.upperCaseFirstChar(this.name);
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(321, 543).append(this.name).toHashCode();
    }

    public boolean isEndFlowValueBooleanLiteral() {
        return isEndFlowValueTrueLiteral() || isEndFlowValueFalseLiteral();
    }

    public boolean isEndFlowValueNullLiteral() {
        return getEndFlowValue().equals("null");
    }

    public boolean isEndFlowValueTrueLiteral() {
        return getEndFlowValue().equals("true");
    }

    public boolean isEndValueFlowKnown() {
        return this.endValueFlow != null;
    }

    public boolean isInitialValueFlowKnown() {
        return this.initialValueFlow != null;
    }

    public boolean isPrivate() {
        return this.protection == Protection.PRIVATE;
    }

    public boolean isStatic() {
        return this.ztatic;
    }

    public void setAnnotations(final List<String> annotations) {
        this.annotations = annotations;
    }

    public void setEndFlowValue(final String endFlowValue) {
        this.endValueFlow = endFlowValue;
    }

    public void setInitialValueFlow(final String initialValue) {
        this.initialValueFlow = initialValue;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setProtection(final Protection protection) {
        this.protection = protection;
    }

    public void setStatic(final boolean ztatic) {
        this.ztatic = ztatic;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String variableNameForType() {
        if (getType() == null) return null;
        return getType().getVariableName();
    }

    private boolean isEndFlowValueFalseLiteral() {
        return getEndFlowValue().equals("false");
    }
}
