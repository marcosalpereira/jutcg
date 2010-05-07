package br.gov.serpro.tools.junit.model;

import java.util.ArrayList;
import java.util.List;


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
		StringBuffer result = new StringBuffer(getName());
		List<FormalParameter> params = getFormalParameters();
		result.append('(');
		for (int i = 0; i < params.size(); i++) {
			if (i > 0) {
				result.append(',');
			}
			result.append(params.get(i).getType().getName());
		}
		result.append(')');
		return result.toString();
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

@Override
public String toString() {
	return this.name;
}

}
