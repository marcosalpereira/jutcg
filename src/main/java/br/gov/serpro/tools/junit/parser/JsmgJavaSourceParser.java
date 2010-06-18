/**
 *
 */
package br.gov.serpro.tools.junit.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

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
			method.setProtection(translateProtection(jsmgMethod));
			if (!jsmgMethod.isVoid()) {
				method.setType(translateTypeName(jsmgMethod.getReturnType()));
			}
			method.setJavaClass(javaClass);
			method.setFormalParameters(translateParameters(jsmgMethod));
			method.setFlows(translateFlows(jsmgMethod, method, jsmgModel));
			methods.add(method);
		}
		return methods;
	}

	private Protection translateProtection(org.jsmg.model.Method jsmgMethod) {
		if (jsmgMethod.isPublic()) {
			return Protection.PUBLIC;
		} else if (jsmgMethod.isPrivate()) {
			return Protection.PRIVATE;
		} else if (jsmgMethod.isProtected()) {
			return Protection.PROTECTED;
		}

		return Protection.DEFAULT;
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

	private List<Flow> translateFlows(org.jsmg.model.Method jsmgMethod, Method method,
			JavaSourceClassModel jsmgClassModel) {
		final List<ExecutionPath> executionsPath = jsmgMethod.getExecutionsPath();
		final List<Flow> flows = new ArrayList<Flow>(executionsPath.size());
		for (final ExecutionPath executionPath : executionsPath) {

			augmentExecutionPathWithOneFlowClassMethodClass(executionPath, jsmgClassModel,
					new HashMap<ExecutionPathNode, List<MethodInvocation>>(0));

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

	/**
	 * Verifies if there are invocations to a method of the class being tested.
	 * If the method called has only one execution path, then the nodes of this path are
	 * inserted in the enclosing execution path.
	 * @param executionPath execution path
	 * @return if the execution path was augmented
	 */
	private Map<ExecutionPathNode, List<MethodInvocation>> augmentExecutionPathWithOneFlowClassMethodClass(
			ExecutionPath executionPath, JavaSourceClassModel jsmgClassModel,
			Map<ExecutionPathNode, List<MethodInvocation>> lastIterationResultMap) {
		final Map<ExecutionPathNode, List<MethodInvocation>> mapNodeWithOwnClassMethodInvocations =
			getMapNodeWithOwnClassMethodInvocations(executionPath, jsmgClassModel);
		final boolean newOwnClassMethodInvocationsFound = mapNodeWithOwnClassMethodInvocations.size()
			> lastIterationResultMap.size();
		if (newOwnClassMethodInvocationsFound) {
			Map<ExecutionPathNode, List<MethodInvocation>> newInvocationsFound =
				getMapWithNewInvocations(lastIterationResultMap, mapNodeWithOwnClassMethodInvocations);
			addNodesFromNewMethodsFound(executionPath, jsmgClassModel, newInvocationsFound);
			// recursive search
			return augmentExecutionPathWithOneFlowClassMethodClass(executionPath, jsmgClassModel,
					mapNodeWithOwnClassMethodInvocations);
		}
		// else, end recursion
		return mapNodeWithOwnClassMethodInvocations;
	}

	private Map<ExecutionPathNode, List<MethodInvocation>> getMapWithNewInvocations(
			Map<ExecutionPathNode, List<MethodInvocation>> lastIterationResultMap,
			final Map<ExecutionPathNode, List<MethodInvocation>> mapNodeWithOwnClassMethodInvocations) {
		Map<ExecutionPathNode, List<MethodInvocation>> newInvocationsFound =
			new HashMap<ExecutionPathNode, List<MethodInvocation>>(mapNodeWithOwnClassMethodInvocations);
		for (ExecutionPathNode alreadyAugmentedNode : lastIterationResultMap.keySet()) {
			newInvocationsFound.remove(alreadyAugmentedNode);
		}
		return newInvocationsFound;
	}

	private void addNodesFromNewMethodsFound(
			ExecutionPath executionPath,
			JavaSourceClassModel jsmgClassModel,
			final Map<ExecutionPathNode, List<MethodInvocation>> mapNodeWithOwnClassMethodInvocations) {
		for (final Entry<ExecutionPathNode, List<MethodInvocation>> nodeWithInvocations : mapNodeWithOwnClassMethodInvocations
				.entrySet()) {
			final List<ExecutionPathNode> nodesToBeInserted = getNodesToBeInsertedExecutionPath(
					jsmgClassModel, nodeWithInvocations);
			addNodesToPath(executionPath, nodeWithInvocations,
					nodesToBeInserted);
		}
	}

	private void addNodesToPath(ExecutionPath executionPath,
			final Entry<ExecutionPathNode, List<MethodInvocation>> nodeWithInvocations,
			final List<ExecutionPathNode> nodesToBeInserted) {
		final ExecutionPathNode pathNode = nodeWithInvocations.getKey();
		final List<ExecutionPathNode> executionPathNodes = executionPath.getExecutionPathNodes();
		final int insertionIndex = executionPathNodes.indexOf(pathNode) + 1;
		if(insertionIndex + 1 <= executionPathNodes.size()) {
			executionPathNodes.addAll(insertionIndex, nodesToBeInserted);
		} else {
			executionPathNodes.addAll(nodesToBeInserted);
		}
	}

	private List<ExecutionPathNode> getNodesToBeInsertedExecutionPath(
			JavaSourceClassModel jsmgClassModel,
			final Entry<ExecutionPathNode, List<MethodInvocation>> nodeWithInvocations) {
		final List<MethodInvocation> invocationsUniqueFlow = nodeWithInvocations.getValue();
		final List<ExecutionPathNode> nodesToBeInserted = new ArrayList<ExecutionPathNode>();
		for (final MethodInvocation methodInvocation : invocationsUniqueFlow) {
			final org.jsmg.model.Method method = jsmgClassModel.getMethod(methodInvocation.getMethodInvoked());
			final ExecutionPath uniqueExecutionPath = method.getExecutionsPath().iterator().next();
			nodesToBeInserted.addAll(uniqueExecutionPath.getExecutionPathNodes());
		}
		return nodesToBeInserted;
	}

	private Map<ExecutionPathNode, List<MethodInvocation>> getMapNodeWithOwnClassMethodInvocations(
			ExecutionPath executionPath, JavaSourceClassModel jsmgClassModel) {
		final Map<ExecutionPathNode, List<MethodInvocation>> mapNodeWithOwnClassMethosInvocations =
			new HashMap<ExecutionPathNode, List<MethodInvocation>>();
		for (final ExecutionPathNode node : executionPath.getExecutionPathNodes()) {
			final List<MethodInvocation> ownClassMethodInvocations = getOwnClassMethodInvocationsWithOneExecutionPath(
					jsmgClassModel, node);
			if(!ownClassMethodInvocations.isEmpty()) {
				mapNodeWithOwnClassMethosInvocations.put(node, ownClassMethodInvocations);
			}
		}
		return mapNodeWithOwnClassMethosInvocations;
	}

	private List<MethodInvocation> getOwnClassMethodInvocationsWithOneExecutionPath(
			JavaSourceClassModel jsmgClassModel, ExecutionPathNode node) {
		final List<MethodInvocation> methodsInvocations = new ArrayList<MethodInvocation>();
		for (final MethodInvocation methodInvocation : node
				.getInternalMethodInvocations()) {
			if (isOwnClassMethodInvocationWithOneExecutionPath(jsmgClassModel, methodInvocation)) {
				methodsInvocations.add(methodInvocation);
			}
		}
		return methodsInvocations;
	}

	private boolean isOwnClassMethodInvocationWithOneExecutionPath(JavaSourceClassModel jsmgClassModel,
			final MethodInvocation methodInvocation) {
		final org.jsmg.model.Method classMethod = jsmgClassModel.getMethod(methodInvocation
								.getMethodInvoked());
		return !methodInvocation.hasInvokerVariable() && classMethod != null
			&& classMethod.getExecutionsPath().size() == 1;
	}

	private List<FlowBranch> translateFlowBranches(ExecutionPath executionPath) {
	    final List<FlowBranch> flowBranches = new ArrayList<FlowBranch>();

        for (final ExecutionPathNode pathNode : executionPath.getExecutionPathNodes()) {
            if(pathNode.isBranch()) {
                final FlowBranch flowBranch = new FlowBranch();
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
