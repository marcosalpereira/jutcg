package br.gov.serpro.tools.junit.model;

import java.util.List;


public class FormalParameter {
	/**
	 * Formal parameter name.
	 */
	private String variableId;

	/**
	 * Field type.
	 */
	private Type type;

	/**
	 * Field's annotations.
	 */
	private List<Type> annotations;

	public List<Type> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Type> annotations) {
		this.annotations = annotations;
	}

	public String getVariableId() {
		return variableId;
	}

	public void setVariableId(String name) {
		this.variableId = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
