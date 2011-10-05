package br.gov.serpro.tools.junit.generate;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.gov.serpro.tools.junit.model.Protection;
import br.gov.serpro.tools.junit.model.Type;
import br.gov.serpro.tools.junit.util.SourceBuilder;

/**
 * A method in test case class.
 */
public class TestMethod {

    /**
     * Method name.
     */
    private String name;

    /**
     * Parent class.
     */
    private JunitTestCase junitTestCase;

    /**
     * Method return type.
     */
    private Type type;

    private final Set<String> annotations = new LinkedHashSet<String>();
    private final Set<String> exceptions = new LinkedHashSet<String>();
    private String javaDoc;
    private final UsedVars usedVars = new UsedVars();
    private final ConfigMocks configMocks = new ConfigMocks();
    private final ConfigInternalState configInternalState = new ConfigInternalState();
    private final InvokeMethod invokeMethod = new InvokeMethod();
    private final Verifys verifys = new Verifys();
    private final Asserts asserts = new Asserts();
    private final GeneralCode generalCode = new GeneralCode();

    private Protection protection;

    public TestMethod() {
    }

    public void setJavaDoc(final String format, final Object... args) {
        this.javaDoc = String.format(format, args);
    }

    GeneralCode getGeneralCode() {
        return this.generalCode;
    }

    UsedVars getUsedVars() {
        return this.usedVars;
    }

    ConfigMocks getConfigMocks() {
        return this.configMocks;
    }

    ConfigInternalState getConfigInternalState() {
        return this.configInternalState;
    }

    InvokeMethod getInvokeMethod() {
        return this.invokeMethod;
    }

    Verifys getVerifys() {
        return this.verifys;
    }

    Asserts getAsserts() {
        return this.asserts;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TestMethod) {
            final TestMethod that = (TestMethod) obj;
            return new EqualsBuilder().append(this.getName(), that.getName())
                    .isEquals();
        }

        return false;
    }

    public Set<String> getAnnotations() {
        return this.annotations;
    }

    public Set<String> getExceptions() {
        return this.exceptions;
    }

    public JunitTestCase getJunitTestCase() {
        return this.junitTestCase;
    }

    public String getName() {
        return this.name;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(123, 445).append(this.getName())
                .toHashCode();
    }

    public void setJunitTestCase(final JunitTestCase junitTestCase) {
        this.junitTestCase = junitTestCase;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setProtection(final Protection protection) {
        this.protection = protection;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public Type getType() {
        if (this.type == null) {
	        return Type.VOID;
        }
        return this.type;
    }

    public Protection getProtection() {
        if (this.protection == null) {
	        return Protection.PUBLIC;
        }
        return this.protection;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String asCode() {
        final SourceBuilder sb = new SourceBuilder();
        if (this.javaDoc != null) {
            sb.appendJavaDoc(this.javaDoc);
        }
        if (!this.annotations.isEmpty()) {
            sb.appendln(this.getAnnotationsAsString());
        }
        sb.append("%s %s %s()", getProtection(), getType(), getName());
        if (!this.exceptions.isEmpty()) {
            sb.append(" throws %s", getExceptionsAsString());
        }
        sb.appendln(" {");
        appendNotEmptySection(sb, this.usedVars);
        appendNotEmptySection(sb, this.configMocks);
        appendNotEmptySection(sb, this.configInternalState);
        appendNotEmptySection(sb, this.invokeMethod);
        appendNotEmptySection(sb, this.verifys);
        appendNotEmptySection(sb, this.asserts);
        appendNotEmptySection(sb, this.generalCode);
        sb.appendln("}");
        return sb.toString();
    }

    private String getAnnotationsAsString() {
        final StringBuilder sb = new StringBuilder();
        for (final Iterator<String> iterator = this.annotations.iterator(); iterator
                .hasNext();) {
            sb.append("@" + iterator.next());
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private String getExceptionsAsString() {
        final StringBuilder sb = new StringBuilder();
        for (final Iterator<String> iterator = this.exceptions.iterator(); iterator
                .hasNext();) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private void appendNotEmptySection(final SourceBuilder sb,
            final MethodSection section) {
        if (section.isNotEmpty()) {
            sb.append(section.asCode());
        }
    }

}
