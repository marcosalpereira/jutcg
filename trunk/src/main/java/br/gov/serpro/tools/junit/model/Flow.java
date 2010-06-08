package br.gov.serpro.tools.junit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
	private SortedSet<Field> readFields = new TreeSet<Field>();

	/**
	 * Fields written on this flow.
	 */
	private SortedSet<Field> writtenFields = new TreeSet<Field>();

	/**
	 * Description of the flow.
	 */
	private String description;


	public SortedSet<Field> getWrittenFields() {
		return writtenFields;
	}

	public void setWrittenFields(SortedSet<Field> writtenFields) {
		this.writtenFields = writtenFields;
	}

	public SortedSet<Field> getReadFields() {
		return readFields;
	}

	public void setReadFields(SortedSet<Field> usedFields) {
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

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Retorna o metodo que foi usado num return neste fluxo.
	 * @return o metodo ou <code>null</code> se nao existir
	 */
	public Method getReturnInvocationMethod() {
		for (final FieldMethodInvocation invocation : invocations) {
			if (invocation.isReturnInvocation()) return invocation.getMethod();
		}
		return null;
	}
}
