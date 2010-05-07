package br.gov.serpro.tools.junit;

import java.util.List;

import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.Flow;
import br.gov.serpro.tools.junit.model.Method;

public class TestCaseMethodGenerator {
	final private Method method;
	final private List<Field> dependencies;

	public TestCaseMethodGenerator(Method method, List<Field> dependencies) {
		this.method = method;
		this.dependencies = dependencies;
	}

	public String generate() {
		SourceBuilder sb = new SourceBuilder();
		for(Flow flow : method.getFlows()) {
			sb.appendln(new TestCaseMethodFlowGenerator(flow, dependencies).generate());
		}
		return sb.toString();
	}

}
