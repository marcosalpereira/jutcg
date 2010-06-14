/**
 *
 */
package br.gov.serpro.tools.junit.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jsmg.JsmgParser;
import org.jsmg.model.Annotation;
import org.jsmg.model.ExecutionPath;
import org.jsmg.model.ExecutionPathNode;
import org.jsmg.model.ImportPath;
import org.jsmg.model.JavaSourceClassModel;
import org.jsmg.model.MethodInvocation;
import org.jsmg.model.Parameter;
import org.jsmg.model.ValueHolder;
import org.jsmg.model.Variable;

import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.FieldMethodInvocation;
import br.gov.serpro.tools.junit.model.Flow;
import br.gov.serpro.tools.junit.model.FormalParameter;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.model.Method;
import br.gov.serpro.tools.junit.model.Protection;
import br.gov.serpro.tools.junit.model.Scope;
import br.gov.serpro.tools.junit.model.Type;
import br.gov.serpro.tools.junit.model.Flow.FlowBranch;

/**
 * Build java model class using the jsmg project's java source parser.
 */
public class JsmgJavaSourceParser implements SourceParser {

	/** {@inheritDoc} */
	public JavaClass parse(File file) throws ParseException {
		final JavaSourceClassModel javaSourceClassModel = JsmgParser.parse(file);
		final JavaClass javaClass = build(javaSourceClassModel);
		return javaClass;
	}

	/**
	 * Builds {@link JavaClass} based on model generated by jsmg.
	 * @param jsmgModel jsmg java source model
	 * @return {@link JavaClass} java source model
	 */
	private JavaClass build(JavaSourceClassModel jsmgModel) {
		final JavaClass javaClass = new JavaClass();
		javaClass.setType(translateType(jsmgModel));
		javaClass.setImports(translateImports(jsmgModel));
		javaClass.setFields(translateFields(jsmgModel));
		javaClass.setMethods(translateMethods(jsmgModel, javaClass));
		javaClass.setPackageName(jsmgModel.getPackageName());
		return javaClass;
	}

	private Type translateType(JavaSourceClassModel jsmgModel) {
		final Type type = new Type();
		type.setName(jsmgModel.getSimpleClassName());
		type.setFullName(jsmgModel.getClassName());
		type.setPrimitive(false);
		return type;
	}

	private List<String> translateImports(JavaSourceClassModel jsmgModel) {
		final List<String> imports = new ArrayList<String>(jsmgModel.getClassImports().size());
		final List<ImportPath> classImports = jsmgModel.getClassImports();
		for (final ImportPath importPath : classImports) {
			imports.add(importPath.getImportPath());
		}
		return imports;
	}

	private List<Field> translateFields(JavaSourceClassModel jsmgModel) {
		final List<Field> fields = new ArrayList<Field>(jsmgModel.getFields().size());
		final List<org.jsmg.model.Field> jsmgFields = jsmgModel.getFields();
		for (final org.jsmg.model.Field jsmgField : jsmgFields) {
			final Field field = new Field();
			field.setName(jsmgField.getVariableName());
			field.setProtection(translateProtection(jsmgField));
			field.setStatic(jsmgField.isStatic());
			field.setType(translateTypeName(jsmgField.getFieldType()));
			field.setAnnotations(translateAnnoatations(jsmgField));
			fields.add(field);
		}
		return fields;
	}

	private List<String> translateAnnoatations(org.jsmg.model.Field jsmgField) {
		final List<Annotation> jsmgAnnotations = jsmgField.getAnnotations();
		final List<String> annotations = new ArrayList<String>(jsmgAnnotations
				.size());
		for (final Annotation annotation : jsmgAnnotations) {
			annotations.add(annotation.getName());
		}
		return annotations;
	}

	private Protection translateProtection(org.jsmg.model.Field jsmgField) {
		if (jsmgField.isPrivate()) {
			return Protection.PRIVATE;
		} else if (jsmgField.isProtected()) {
			return Protection.PROTECTED;
		} else if (jsmgField.isPublic()) {
			return Protection.PUBLIC;
		} else {
			return Protection.DEFAULT;
		}
	}

	private Type translateTypeName(String typeName) {
		final Type type = new Type();
		type.setName(typeName);
		type.setPrimitive(isPrimitive(typeName));
		return type;
	}

	private boolean isPrimitive(String type) {
		return "byte,short,int,long,float,double,boolean".indexOf(type) != -1;
	}

	private List<Method> translateMethods(JavaSourceClassModel jsmgModel, JavaClass javaClass) {
		final List<org.jsmg.model.Method> jsmgMethods = jsmgModel.getMethods();
		final List<Method> methods = new ArrayList<Method>(jsmgMethods.size());
		for (final org.jsmg.model.Method jsmgMethod : jsmgMethods) {
			final Method method = new Method();
			method.setName(jsmgMethod.getName());
			method.setProtection(null);
			if (!jsmgMethod.isVoid()) {
				method.setType(translateTypeName(jsmgMethod.getReturnType()));
			}
			method.setJavaClass(javaClass);
			method.setFormalParameters(translateParameters(jsmgMethod));
			method.setFlows(translateFlows(jsmgMethod, method));
			methods.add(method);
		}
		return methods;
	}

	private List<FormalParameter> translateParameters(
			org.jsmg.model.Method jsmgMethod) {
		final List<Parameter> jsmgParameters = jsmgMethod.getParameters();
		final List<FormalParameter> parameters = new ArrayList<FormalParameter>(jsmgParameters.size());
		for (final Parameter parameter : jsmgParameters) {
			final FormalParameter formalParameter = new FormalParameter();
			formalParameter.setVariableId(parameter.getVariableId());
			formalParameter.setType(translateTypeName(parameter.getType()));
			parameters.add(formalParameter);
		}
		return parameters;
	}

	private List<Flow> translateFlows(org.jsmg.model.Method jsmgMethod, Method method) {
		final List<ExecutionPath> executionsPath = jsmgMethod.getExecutionsPath();
		final List<Flow> flows = new ArrayList<Flow>(executionsPath.size());
		for (final ExecutionPath executionPath : executionsPath) {
			final Flow flow = new Flow();
			flow.setName(executionPath.getName());
			flow.setFlowBranches(translateFlowBranches(executionPath));
			flow.setMethod(method);
			flow.setReadFields(translateReadFields(executionPath, method.getJavaClass()));
			flow.setWrittenFields(translateWrittenFields(executionPath, method.getJavaClass()));
			flow.setInvocations(translateInvocations(executionPath, method));
			flows.add(flow);
		}
		return flows;
	}

	private List<FlowBranch> translateFlowBranches(ExecutionPath executionPath) {
	    final List<FlowBranch> flowBranches = new ArrayList<FlowBranch>();

        for (final ExecutionPathNode pathNode : executionPath.getExecutionPathNodes()) {
            if(pathNode.isBranch()) {
                FlowBranch flowBranch = new FlowBranch();
                flowBranch.setEnter(pathNode.isEntersConditionalExpression());
                flowBranch.setExpression(pathNode.getNodeImage());
                flowBranches.add(flowBranch);
            }
        }
        return flowBranches;
    }

    private List<FieldMethodInvocation> translateInvocations(
			ExecutionPath executionPath, Method method) {
		final List<FieldMethodInvocation> fieldMethodInvocations = new ArrayList<FieldMethodInvocation>();
		final List<MethodInvocation> methodInvocations = executionPath.getInternalMethodInvocations();
		for (final MethodInvocation methodInvocation : methodInvocations) {
			if (methodInvocation.hasInvokerVariable()
					&& methodInvocation.getInvokerVariable().isClassScope()) {
				fieldMethodInvocations.add(translateFieldMethodInvocation(methodInvocation, method));
			}
		}
		return fieldMethodInvocations;
	}

	private FieldMethodInvocation translateFieldMethodInvocation(
			MethodInvocation methodInvocation, Method method) {
		final FieldMethodInvocation fieldMethodInvocation = new FieldMethodInvocation();
		fieldMethodInvocation.setInvokedAtField(method.getJavaClass()
				.searchField(methodInvocation.getInvokerVariable().getVariableId()));
		fieldMethodInvocation.setMethod(translateInvokedMethod(methodInvocation));
		fieldMethodInvocation.setReturnInvocation(methodInvocation.isReturnedInvocation());
		if (methodInvocation.isAssigned()) {
			fieldMethodInvocation.setAssignedVariable(
					translateVariable(methodInvocation.getAssignedVariable()));
		}
		fieldMethodInvocation.setArguments(translateArgumentList(methodInvocation));

		return fieldMethodInvocation;
	}

	private br.gov.serpro.tools.junit.model.Variable translateVariable(
			Variable assignedVariable) {
		final br.gov.serpro.tools.junit.model.Variable variable = new br.gov.serpro.tools.junit.model.Variable();
		variable.setName(assignedVariable.getVariableId());
		variable.setScope(translateScope(assignedVariable.getScope()));
		variable.setType(translateType(assignedVariable.getType()));
		return variable;
	}

	private Scope translateScope(org.jsmg.model.Scope scope) {
		if(scope.isLocalScope()) {
			return Scope.LOCAL_SCOPE;
		} else if(scope.isMethodScope()) {
			return Scope.METHOD_SCOPE;
		} else {
			return Scope.CLASS_SCOPE;
		}
	}

	private Method translateInvokedMethod(MethodInvocation methodInvocation) {
		final Method method = new Method();
		method.setName(methodInvocation.getMethodInvoked());
		if(methodInvocation.getReturnedType() != null) {
			method.setType(translateType(methodInvocation.getReturnedType()));
		}
		return method;
	}

	private Type translateType(String returnedType) {
		final Type type = new Type();
		type.setName(returnedType);
		type.setPrimitive(isPrimitive(returnedType));
		return type;
	}

	private List<String> translateArgumentList(MethodInvocation methodInvocation) {
		final List<ValueHolder> jsmgArgs = methodInvocation.getArgumentsList();
		final List<String> arguments = new ArrayList<String>(jsmgArgs.size());
		for (final ValueHolder argument : jsmgArgs) {
			arguments.add(argument.getImage());
		}
		return arguments;
	}

	private SortedSet<Field> translateWrittenFields(ExecutionPath executionPath,
			JavaClass javaClass) {
		final Set<Variable> writtenVariables = executionPath.getVariablesWritten();
		return getFields(javaClass, writtenVariables);
	}

	private SortedSet<Field> translateReadFields(ExecutionPath executionPath, JavaClass javaClass) {
		final Set<Variable> readVariables = executionPath.getReadVariables();
		return getFields(javaClass, readVariables);
	}

	/**
	 * Returns fields that are present in the variables set.
	 * @param javaClass java class
	 * @param variables variables set
	 * @return fields in the set
	 */
	private SortedSet<Field> getFields(JavaClass javaClass, final Set<Variable> variables) {
		final SortedSet<Field> fieldsRead = new TreeSet<Field>();
		for (final Variable variable : variables) {
			if (variable.isClassScope()) {
				fieldsRead.add(javaClass.searchField(variable.getVariableId()));
			}
		}
		return fieldsRead;
	}
}
