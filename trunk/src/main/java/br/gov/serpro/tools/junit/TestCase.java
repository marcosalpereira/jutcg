package br.gov.serpro.tools.junit;

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.jaxme.js.JavaField;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.Parameter;

public class TestCase {
	private JavaSource javaSource;
	private List<String> rawSource;
	private List<JavaField> dependencies;

	public TestCase(JavaSource js) {
		this.javaSource = js;
	}

	public void setRawSource(List<String> rawSource) {
		this.rawSource = rawSource;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		dependencies = selectDependencies();

		sb.append(generatePackageName());
		sb.append(generateImports());
		sb.append(generateClassStart());
		sb.append(generateFields());
		sb.append(generateSetup());
		sb.append(generateMethods());
		sb.append(generateClassEnd());
		return sb.toString();
	}

	private List<JavaField> selectDependencies() {
		JavaField[] fields = javaSource.getFields();
		List<JavaField> deps = new ArrayList<JavaField>();
		for (int i = 0; i < fields.length; i++) {
			if (!ignoreField(fields[i])) {
				deps.add(fields[i]);
			}
		}
		return deps;
	}

	private String generateMethods() {
		StringBuilder sb = new StringBuilder();
		JavaMethod[] methods = javaSource.getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (!ignoreMethod(methods[i])) {
				sb.append(generateMethod(methods[i]));
			}
		}
		return sb.toString();
	}

	private String generateMethod(JavaMethod javaMethod) {
		StringBuilder sb = new StringBuilder();
		sb.append(generateMethodComment(javaMethod));
		sb.append(generateMethodBody(javaMethod));
		return sb.toString();
	}

	private String generateMethodBody(JavaMethod javaMethod) {
		StringBuilder sb = new StringBuilder();
		generateMethodBody_startMethod(sb, javaMethod);
		generateMethodBody_UsedVars(sb, javaMethod);
		generateMethodBody_ConfigMocks(sb, javaMethod);
		generateMethodBody_ConfigInternalState(sb);
		generateMethodBody_InvokeMethod(sb, javaMethod);
		generateMethodBody_CheckMocks(sb);
		generateMethodBody_Verifys(sb, javaMethod);
		generateMethodBody_endMethod(sb);
		return sb.toString();
	}

	/**
	 * @param sb
	 */
	private void generateMethodBody_endMethod(StringBuilder sb) {
		sb.append("\n}");
	}

	private void generateMethodBody_Verifys(StringBuilder sb, JavaMethod javaMethod) {
		sb.append("\n\n// verificacoes do resultado do metodo sendo testado");
		if (!javaMethod.isVoid()) {

			if (selectByPassMethodMock(javaMethod) == null) {
				sb.append(String
						.format("\n%s esperado = %s;", javaMethod.getType().getClassName(),
								newValueForType(javaMethod.getType())));
			}
			sb.append("\nassertEquals(esperado, real);");
		}
	}

	/**
	 * @param sb
	 * @param javaMethod
	 * @param params
	 */
	private void generateMethodBody_InvokeMethod(StringBuilder sb, JavaMethod javaMethod) {
		sb.append("\n\n// invocar metodo sendo testado");
		String strParams = concatMethodParams(javaMethod.getParams());
		if (javaMethod.isVoid()) {
			sb.append(String.format("\n%s.%s(%s);",
					variableNameForType(javaSource.getClassName()),
					javaMethod.getName(),
					strParams));
		} else {
			sb.append(String.format("\nfinal %s real = %s.%s(%s);",
					javaMethod.getType().getClassName(),
					variableNameForType(javaSource.getClassName()),
					javaMethod.getName(),
					strParams));

		}
	}

	/**
	 * @param params
	 * @return
	 */
	private String concatMethodParams(Parameter[] params) {
		String strParams = "";
		for (Parameter p : params) {
			if (!strParams.isEmpty()) strParams += ",";
			strParams += p.getName();
		}
		return strParams;
	}

	/**
	 * @param sb
	 */
	private void generateMethodBody_ConfigInternalState(StringBuilder sb) {
		if (isAtView()) {
			sb.append("\n\n// configurando estado interno da classe sob teste");
		}
	}

	/**
	 * @param sb
	 * @param javaMethod
	 */
	private void generateMethodBody_startMethod(StringBuilder sb, JavaMethod javaMethod) {
		sb.append("\n@Test");
		sb.append(String.format("\npublic void test%s() {",
				javaMethod.getName().substring(0,1).toUpperCase()
				+ javaMethod.getName().substring(1)));
	}

	private void generateMethodBody_ConfigMocks(StringBuilder sb, JavaMethod javaMethod) {
		if (dependencies.isEmpty()) return;
		sb.append("\n\n// configurar mocks");
		String mock = selectByPassMethodMock(javaMethod);
		if (mock == null) return;

		String strParams = concatMethodParams(javaMethod.getParams());

		if (javaMethod.isVoid()) {
			sb.append(String.format("\n%s.%s(%s);", mock, javaMethod.getName(), strParams));
		} else {
			sb.append(String.format("\nfinal %s esperado = %s;", javaMethod.getType().getClassName(), newValueForType(javaMethod.getType())));
			sb.append(String.format("\nexpect(%s.%s(%s)).andReturn(esperado);", mock, javaMethod.getName(), strParams));
		}
		sb.append(String.format("\nreplay(%s);", mock));

		//List<String> mocks = guessMocksFromMethod(javaMethod);
	}


	private String newValueForType(JavaQName type) {

		if (!type.isPrimitive()) {
			return String.format("new %s(1)", type.getClassName());
		}

		if (type.getClassName().equals("boolean")) {
			return "false";
		}

		return "0";

	}

	private String selectByPassMethodMock(JavaMethod javaMethod) {
		List<String> depCalls = new ArrayList<String>();
		String src = selectMethodSource(javaMethod);
		if (src == null) return null;

		for(JavaField field : dependencies) {
			if (src.indexOf(field.getName()) != -1) {
				depCalls.add(field.getName());
			}
		}
		if (depCalls.size() == 1 && src.indexOf(';') == src.lastIndexOf(';')) {
			return depCalls.get(0);
		}

		return null;
	}

	private String selectMethodSource(JavaMethod javaMethod) {
		int index = searchRawSource("^.*" + javaMethod.getType().getClassName() + ".*" + javaMethod.getName() + ".*$" );
		if (index == -1) return null;

		StringBuilder src = new StringBuilder();
		int abre = 0;
		int fecha = 0;
		for(int i = index; i<rawSource.size(); i++) {
			String lin = rawSource.get(i);
			src.append(lin);
			if (lin.indexOf('{') != -1) abre += 1;
			if (lin.indexOf('}') != -1) fecha += 1;
			if (abre > 0 && abre == fecha) {
				break;
			}
		}

		return src.toString();

	}


	private void generateMethodBody_UsedVars(StringBuilder sb, JavaMethod javaMethod) {
		Parameter[] params = javaMethod.getParams();

		if (params.length > 0) {
			sb.append("\n\n// variaveis usadas");

			for (Parameter p : params) {
				sb.append(String.format("\nfinal %s %s = %s;", p.getType().getClassName(), p.getName(), newValueForType(p.getType())));

//				if (p.getType().isPrimitive()) {
//					if (p.getType().getClassName().equals("boolean")) {
//						sb.append(String.format("\nfinal %s %s = false;", p.getType().getClassName(), p.getName()));
//					} else {
//						sb.append(String.format("\nfinal %s %s = 0;", p.getType().getClassName(), p.getName()));
//					}
//				} else {
//					sb.append(String.format("\nfinal %s %s = new %s(1);", p.getType()
//							.getClassName(), p.getName(), p.getType().getClassName()));
//				}
			}
		}
	}

	private void generateMethodBody_CheckMocks(StringBuilder sb) {
		if (dependencies.isEmpty()) return;

		sb.append("\n\n// checar estados dos mocks");

		String mocks = "";
		for (JavaField dep : dependencies) {
			mocks += dep.getName() + ",";
		}
		sb.append("\nverify(" + mocks.substring(0, mocks.length()-1) + ");");
	}

	private String generateMethodComment(JavaMethod javaMethod) {
		return encloseComment("Teste para o metodo {@link "
				+ javaMethod.getJavaSource().getClassName() + "#" + javaMethod.getLoggingSignature()
				+ "}.");
	}

	private boolean ignoreMethod(JavaMethod javaMethod) {
		if (javaMethod.getProtection().equals(JavaSource.PRIVATE)) {
			return true;
		}
		if (javaMethod.getName().startsWith("is")
				|| javaMethod.getName().startsWith("get")
				|| javaMethod.getName().startsWith("set")) {
			return true;
		}
		return false;
	}

	private String generateSetup() {
		StringBuilder sb = new StringBuilder();
		final String variableNameForType = variableNameForType(javaSource.getClassName());

		if (isAtDao()) {
			sb.append("\n\n/** {@inheritDoc} */");
			sb.append("\n@Override");
			sb.append("\nprotected IDataSet getDataSet() {");
			final String xmlds = variableNameForType.replace("DaoBean", "") + "DS.xml" ;
			sb.append(String.format("\n return recuperarDataSet(\"%s\");",  xmlds));
			sb.append("\n}");
		}

		if (isAtView()) {
			sb.append("\n\n/** {@inheritDoc} */");
			sb.append("\n@Override");
			sb.append("\nprotected void setUp() throws Exception {");
			sb.append("\n  super.setUp();");
		} else {
			sb.append("\n\n/** Configuracoes iniciais */");
			sb.append("\n@Before");
			sb.append("\nprotected void setUp() {");
		}
		sb.append(String.format("\n  %s = new %s();\n",
				variableNameForType,
				javaSource.getClassName()
				));

		if (isAtDao()) {
			sb.append("\n" + variableNameForType + ".setEntityManager(getEntityManager());");
		}

		for (JavaField dep : dependencies) {
			sb.append( criarMock(dep) );
			sb.append( "\n" );
		}

		sb.append("\n}");
		return sb.toString();
	}

	private String criarMock(JavaField javaField) {
		final String fieldClassName = javaField.getType().getClassName();

		final String fieldVariable = variableNameForType(fieldClassName);
		return String.format("\n%s = createStrictMock(%s);\n%s.set%s(%s);",
				fieldVariable,
				fieldClassName + ".class",
				variableNameForType(javaSource.getClassName()),
				fieldClassName,
				fieldVariable
				);
	}

	private String generateFields() {
		StringBuilder sb = new StringBuilder();
		sb.append(generateField(javaSource.getClassName(), "Classe sendo testada"));

		for (JavaField dep : dependencies) {
			sb.append(generateField(dep));
			sb.append("\n");
		}
		return sb.toString();
	}

	private String generateField(JavaField javaField) {
		final String fieldType = javaField.getType().getClassName();
		return generateField(fieldType, "{@link " + fieldType + "}.");


	}

	private String generateField(String fieldType, String comment) {
		return encloseComment(comment)
			+ "\nprivate " + fieldType + " " + variableNameForType(fieldType) + ";";
	}

	private String variableNameForType(String fieldType) {
		return fieldType.substring(0, 1).toLowerCase() + fieldType.substring(1);
	}

	private boolean ignoreField(JavaField javaField) {
		if (javaField.isStatic()) return true;

		if (!javaField.getProtection().equals(JavaSource.PRIVATE)) return true;

		String annotations = selectFieldAnnotations(javaField);

		if (annotations.indexOf("@In") != -1 || annotations.indexOf("@EJB") != -1) {
			return false;
		}

		return true;
	}

	/**
	 * @param javaField
	 * @return
	 */
	private String selectFieldAnnotations(JavaField javaField) {
		int i = searchRawSource("private .*" + javaField.getName() + " *;");
		if (i == -1) return "";
		i--;
		for (; i>=0; i--) {
			String linha = rawSource.get(i);
			if (!isComment(linha)) {
				break;
			}
		}
		String linha = rawSource.get(i);
		return linha;
	}

	private boolean isComment(String linha) {
		return linha.startsWith("/*") || linha.startsWith("*") || linha.startsWith("//");
	}

	private int searchRawSource(String pattern) {
		int i = 0;
		for (String s : rawSource) {
			if (s.matches(pattern)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private String generateImports() {
		StringBuilder sb = new StringBuilder();

		for (JavaQName javaQName : javaSource.getImports()) {
			sb.append(String.format("\nimport %s;" , javaQName.toString()));
		}

		sb.append("\nimport static org.easymock.EasyMock.*;");
		sb.append("\nimport org.junit.*;");
		sb.append("\nimport static org.junit.Assert.*;");
		if (isAtView()) {
			sb.append("\nimport br.gov.esaf.sgc.view.JsfTestCase;");
		}
		return sb.toString();
	}

	private String generateClassEnd() {
		return "\n}";
	}

	private String generateClassStart() {
		String comment = encloseComment("Testes unitarios para a classe {@link " + javaSource.getClassName() + "}.");

		String cls =  "\npublic class Test" + javaSource.getClassName();
		if (isAtView()) {
			cls += " extends JsfTestCase {";
		} else if (isAtDao()) {
			cls += " extends HibernateTestCase {";
		} else {
			cls += " {";
		}

		return comment + cls;
	}

	private boolean isAtView() {
		return javaSource.getPackageName().indexOf("br.gov.esaf.sgc.view") != -1;
	}

	private boolean isAtDao() {
		return javaSource.getPackageName().indexOf("br.gov.esaf.sgc.dao") != -1;
	}

	private String encloseComment(String string) {
		return "\n/**\n * " + string + " \n */";
	}

	private String generatePackageName() {
		return "package " + javaSource.getPackageName() + ";";
	}

}
