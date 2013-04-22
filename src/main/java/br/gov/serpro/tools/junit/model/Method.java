package br.gov.serpro.tools.junit.model;

import static br.gov.serpro.tools.junit.util.GeneratorHelper.lowerCaseFirstChar;
import static br.gov.serpro.tools.junit.util.GeneratorHelper.plural;
import static br.gov.serpro.tools.junit.util.GeneratorHelper.upperCaseFirstChar;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.gov.serpro.tools.junit.util.GeneratorHelper;

/**
 * Reprsents a method in java class.
 */
public class Method {
    /**
     * Method name.
     */
    private String name;

    /**
     * Method return type.
     */
    private Type type;

    /**
     * Method's parameters.
     */
    private List<FormalParameter> formalParameters = new ArrayList<FormalParameter>();

    /**
     * Method protection.
     */
    private Protection protection;

    private List<Flow> flows = new ArrayList<Flow>();

    /**
     * Parent class.
     */
    private JavaClass javaClass;

    public List<Flow> getFlows() {
        return this.flows;
    }

    public void setFlows(final List<Flow> flows) {
        this.flows = flows;
    }

    public void setJavaClass(final JavaClass javaClass) {
        this.javaClass = javaClass;
    }

    public JavaClass getJavaClass() {
        return this.javaClass;
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

    public void setType(final Type returnType) {
        this.type = returnType;
    }

    public List<FormalParameter> getFormalParameters() {
        return this.formalParameters;
    }

    public void setFormalParameters(final List<FormalParameter> params) {
        this.formalParameters = params;
    }

    public Protection getProtection() {
        return this.protection;
    }

    public void setProtection(final Protection protection) {
        this.protection = protection;
    }

    public boolean isVoid() {
        return this.type == null;
    }

    /**
     * <p>
     * Returns the abbreviated method signature: The method name, followed by
     * the parameter types. This is typically used in logging statements.
     * </p>
     */
    public String getLoggingSignature() {
        final StringBuilder result = new StringBuilder(getName());
        result.append('(');
        result.append(getFormalParametersAsString(", "));
        result.append(')');
        return result.toString();
    }

    /**
     * @param result
     */
    private String getFormalParametersAsString(final String separator) {
        final StringBuilder ret = new StringBuilder();
        final List<FormalParameter> params = getFormalParameters();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                ret.append(separator);
            }
            ret.append(params.get(i).getType().getName());
        }
        return ret.toString();
    }

    public boolean isPrivate() {
        return this.protection == Protection.PRIVATE;
    }

    public boolean isGetter() {
    	final String fieldName;
		if (name.startsWith("is")) {
        	fieldName = GeneratorHelper.lowerCaseFirstChar(name.substring(2));

    	} else if (name.startsWith("get")) {
        	fieldName = GeneratorHelper.lowerCaseFirstChar(name.substring(3));
    	} else {
    		fieldName = null;
    	}
    	return fieldName != null && javaClass.existsField(fieldName);
    }

    public boolean isSetter() {
    	final String fieldName;
		if (name.startsWith("set")) {
        	fieldName = GeneratorHelper.lowerCaseFirstChar(name.substring(3));
    	} else {
    		fieldName = null;
    	}
    	return fieldName != null && javaClass.existsField(fieldName);
    }

    public boolean isNotVoid() {
        return !isVoid();
    }

    /**
     * The name for a method that tests this method at flow informed.
     *
     * @return the name founded
     */
    public String getNameForTest(final Flow flow) {
        String ret = "test" + upperCaseFirstChar(getName());
        if (isOverload()) {
            ret += getFormalParametersAsString("");
        }
        if (getFlows().size() > 1) {
            ret += flow.getName();
        }
        return ret;
    }

    private boolean isOverload() {
        return getJavaClass().isAnOverloadedMethod(this);
    }

    public String getVarNameFromReturningMock() {
	    if (type.isCollection()) {
	    	final Type[] generic = type.getGeneric();
	    	if (generic != null) {
	    		return lowerCaseFirstChar(plural(generic[0].getName()));
	    	}
	    	return lowerCaseFirstChar(type.getName());
	    }
	    if (type.isPrimitive()) {
	    	return lowerCaseFirstChar(this.getName());
	    }
	    return lowerCaseFirstChar(type.getName());
    }

	public String getVarNameForRealContentInvocation() {
	    if (type.isCollection()) {
	    	final Type[] generic = type.getGeneric();
	    	if (generic != null) {
	    		return lowerCaseFirstChar(plural(generic[0].getName())) + "Real";
	    	}
	    	return lowerCaseFirstChar(type.getName()) + "Real";
	    }
	    if (type.isPrimitive()) {
	    	return lowerCaseFirstChar(this.getName()) + "Real";
	    }
	    return lowerCaseFirstChar(type.getName()) + "Real";
	}




    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Method) {
            final Method that = (Method) obj;
            return new EqualsBuilder().append(this.getLoggingSignature(),
                    that.getLoggingSignature()).isEquals();
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(123, 445).append(this.getLoggingSignature()).toHashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }

}
