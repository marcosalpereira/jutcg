package br.gov.serpro.tools.junit.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

	private List<FlowBranch> flowBranches = new ArrayList<FlowBranch>();

	public void setFlowBranches(List<FlowBranch> flowBranches) {
		this.flowBranches = flowBranches;
	}

	public List<FlowBranch> getFlowBranches() {
		return flowBranches;
	}

	/**
	 * A branch on a flow.
	 */
	public static class FlowBranch {

		/**
		 * Branch expression.
		 */
		private String expression;

		/**
		 * If enter or not at branch.
		 */
		private boolean enter;

		public String getExpression() {
			return expression;
		}

		public void setExpression(String expression) {
			this.expression = expression;
		}

		public boolean isEnter() {
			return enter;
		}

		public void setEnter(boolean enter) {
			this.enter = enter;
		}

	}

	public SortedSet<Field> getWrittenFields() {
		return writtenFields;
	}

	public void setWrittenFields(SortedSet<Field> writtenFields) {
		this.writtenFields = writtenFields;
	}

	public SortedSet<Field> getReadFields() {
		return readFields;
	}

	public List<Field> selectNonStaticReadFields() {
		final List<Field> ret = new ArrayList<Field>();
		for (Field field : getReadFields()) {
			if (!field.isStatic()) {
				ret.add(field);
			}
		}
		return ret;
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

	/**
	 * Retorna o metodo que foi usado num return neste fluxo.
	 *
	 * @return o metodo ou <code>null</code> se nao existir
	 */
	public Method getReturnInvocationMethod() {
		for (final FieldMethodInvocation invocation : invocations) {
			if (invocation.isReturnInvocation())
				return invocation.getMethod();
		}
		return null;
	}

    public Set<Field> getReadWrittensFields() {
        Set<Field> ret = new HashSet<Field>(getReadFields());
        ret.addAll(getWrittenFields());
        return ret;
    }

}
