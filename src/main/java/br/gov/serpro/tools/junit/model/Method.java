package br.gov.serpro.tools.junit.model;

import static br.gov.serpro.tools.junit.util.GeneratorHelper.upperCaseFirstChar;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


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
		return flows;
	}

	public void setFlows(List<Flow> flows) {
		this.flows = flows;
	}

	public void setJavaClass(JavaClass javaClass) {
		this.javaClass = javaClass;
	}

	public JavaClass getJavaClass() {
		return javaClass;
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

	public void setType(Type returnType) {
		this.type = returnType;
	}

	public List<FormalParameter> getFormalParameters() {
		return formalParameters;
	}

	public void setFormalParameters(List<FormalParameter> params) {
		this.formalParameters = params;
	}

	public Protection getProtection() {
		return protection;
	}

	public void setProtection(Protection protection) {
		this.protection = protection;
	}

	public boolean isVoid() {
		return type == null;
	}

	/**
	 * <p>
	 * Returns the abbreviated method signature: The method name, followed by the parameter
	 * types. This is typically used in logging statements.
	 * </p>
	 */
	public String getLoggingSignature() {
		StringBuilder result = new StringBuilder(getName());
		result.append('(');
		result.append(getFormalParametersAsString(", "));
		result.append(')');
		return result.toString();
	}

    /**
     * @param result
     */
    private String getFormalParametersAsString(String separator) {
        StringBuilder ret = new StringBuilder();
        List<FormalParameter> params = getFormalParameters();
		for (int i = 0; i < params.size(); i++) {
			if (i > 0) {
				ret.append(separator);
			}
			ret.append(params.get(i).getType().getName());
		}
		return ret.toString();
    }

	public boolean isPrivate() {
		return protection == Protection.PRIVATE;
	}

	public boolean isGetter() {
		return getName().startsWith("is") || getName().startsWith("get");
	}

	public boolean isSetter() {
		return getName().startsWith("set");
	}

	public boolean isNotVoid() {
		return !isVoid();
	}

	/**
	 * The name for a method that tests this method at flow informed.
	 * @return the name founded
	 */
	public String getNameForTest(Flow flow) {
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



    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Method) {
            Method that = (Method) obj;
            return new EqualsBuilder()
                .append(this.getLoggingSignature(), that.getLoggingSignature())
                .isEquals();
        }

        return false;
    }

    /**{@inheritDoc}*/
    @Override
    public int hashCode() {
        return new HashCodeBuilder(123, 445)
            .append(this.getLoggingSignature())
            .toHashCode();
    }

	@Override
	public String toString() {
	    return this.name;
	}


}
