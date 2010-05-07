package br.gov.serpro.tools.junit.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Flow {

	/**
	 * Flow name.
	 */
	private String name = "";

	/**
	 * Invocations for this flow.
	 */
	private List<FieldMethodInvocation> invocations = new ArrayList<FieldMethodInvocation>();

	/**
	 * Method that this flow belongs to.
	 */
	private Method method;

	/**
	 * Fields read on this flow.
	 */
	private Set<Field> readFields = new HashSet<Field>();

	/**
	 * Fields written on this flow.
	 */
	private Set<Field> writtenFields = new HashSet<Field>();


	public Set<Field> getWrittenFields() {
		return writtenFields;
	}

	public void setWrittenFields(Set<Field> writtenFields) {
		this.writtenFields = writtenFields;
	}

	public Set<Field> getReadFields() {
		return readFields;
	}

	public void setReadFields(Set<Field> usedFields) {
		this.readFields = usedFields;
	}

	public List<FieldMethodInvocation> getInvocations() {
		return invocations;
	}

	public void setInvocations(List<FieldMethodInvocation> invocations) {
		this.invocations = invocations;
	}

	public Method getMethod() {
		return this.method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna o metodo que foi usado num return neste fluxo.
	 * @return o metodo ou <code>null</code> se nao existir
	 */
	public Method getReturnInvocationMethod() {
		for (FieldMethodInvocation invocation : invocations) {
			if (invocation.isReturnInvocation() ) return invocation.getMethod();
		}
		return null;
	}



}
