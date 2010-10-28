package br.gov.serpro.tools.junit.generate;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.gov.serpro.tools.junit.model.Protection;
import br.gov.serpro.tools.junit.model.Type;
import br.gov.serpro.tools.junit.util.SourceBuilder;

public class TestField {
    /**
     * Field name.
     */
    private String name;

    /**
     * Field type.
     */
    private Type type;

    /**
     * Field protection.
     */
    private Protection protection;

    private String javaDoc;

    public void setProtection(final Protection protection) {
        this.protection = protection;
    }

    public Protection getProtection() {
        return this.protection;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Type getType() {
        return this.type;
    }

    public void setJavaDoc(final String format, final Object... args) {
        this.javaDoc = String.format(format, args);
    }

    public void setType(final Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof TestField)) {
            return false;
        }
        final TestField o = (TestField) obj;
        return new EqualsBuilder().append(getName(), o.getName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(321, 543).append(this.name).toHashCode();
    }

    public String asCode() {
        final SourceBuilder sb = new SourceBuilder();
        sb.appendJavaDoc(this.javaDoc);
        sb.appendln("%s %s %s;", getProtection(), getType(), getName());
        return sb.toString();
    }
}
