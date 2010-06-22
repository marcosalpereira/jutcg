package br.gov.serpro.tools.junit;

import java.util.ArrayList;
import java.util.List;

import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.model.Method;
import br.gov.serpro.tools.junit.model.Type;

public class TestCaseGenerator {
	private static final String IMPL_DAO_SUFIX = "DaoBean";

	final private JavaClass classUnderTest;
	final private List<Field> dependencies;
	final private List<Method> selectedMethods;
	final private String varNameForClassUnderTest;

	public TestCaseGenerator(JavaClass classUnderTest) {
		GlobalFlags.init(); 
		this.classUnderTest = classUnderTest;
		this.dependencies = selectDependencies();
		this.selectedMethods = selectMethods();
		this.varNameForClassUnderTest = classUnderTest.variableNameForType();
	}

	public String generate() {
		final SourceBuilder sb = new SourceBuilder();
		sb.append(generatePackageName());
		sb.append(generateImports());
		sb.append(generateClassStart());
		sb.append(generateFields());
		sb.append(generateCreateMockMethods());
		sb.append(generateAbstractImpls());
		sb.append(generateSetup());
		sb.append(generateMethods());
		sb.append(generateClassEnd());
		return sb.toString();
	}

	String generateCreateMockMethods() {
		final SourceBuilder sb = new SourceBuilder();
		for (final Field dependency : dependencies) {
		    if (classUnderTest.existsAnyInvocation(dependency)) {
		        sb.append(generateCreateMockMethod(dependency));
		    }
		}
		return sb.toString();
	}

	String generateCreateMockMethod(Field dependency) {
		final SourceBuilder sb = new SourceBuilder();
		sb.appendln();
		sb.appendJavaDoc("Cria o mock {@link %s} e seta na classe sendo testada.\n" +
				"@return o mock criado", dependency.getType());
		sb.appendln("private %1$s criarMock%1$s() {", dependency.getType());
		sb.appendln("  %1$s mock = createStrictMock(%1$s.class);", dependency.getType());
		sb.appendln("  %s.set%s(mock);", varNameForClassUnderTest,
		    GeneratorHelper.upperCaseFirstChar(dependency.getName()));
		sb.appendln("  return mock;");
		sb.appendln("}");
		return sb.toString();
	}

	private List<Method> selectMethods() {
		final List<Method> Methods = classUnderTest.getMethods();
		final List<Method> ret = new ArrayList<Method>();
		for (final Method Method : Methods) {
			if (!ignoreMethod(Method)) {
				ret.add(Method);
			}
		}
		return ret;
	}

	private List<Field> selectDependencies() {
		final List<Field> fields = classUnderTest.getFields();
		final List<Field> ret = new ArrayList<Field>();
		for (final Field field : fields) {
			if (!ignoreField(field)) {
				ret.add(field);
			}
		}
		return ret;
	}

	String generateMethods() {
		final SourceBuilder sb = new SourceBuilder();
		for (final Method method : selectedMethods) {
			sb.append(
			    new TestCaseMethodGenerator(method, dependencies)
					.generate());
		}
		return sb.toString();
	}

	private boolean ignoreMethod(Method method) {
		if (method.isPrivate() || method.isGetter() || method.isSetter()) {
			return true;
		}
		return false;
	}

	/**
	 * Gerar metodos que a classe de teste precisa implementar.
	 * @return o codigo gerado
	 */
	String generateAbstractImpls() {
		if (!classUnderTest.isAtDao()) {
			return "";
		}
		final SourceBuilder sb = new SourceBuilder();
		final String dsxml = varNameForClassUnderTest.replace(IMPL_DAO_SUFIX, "") + "DS.xml";
		sb.appendln()
			.appendln("/** {@inheritDoc} */")
    		.appendln("@Override")
    		.appendln("protected IDataSet getDataSet() {")
    		.appendln("  return recuperarDataSet(\"%s\");", dsxml)
    		.appendln("}");
		return sb.toString();

	}

	String generateSetup() {
		final SourceBuilder sb = new SourceBuilder();
		sb.appendln();
		if (classUnderTest.isAtView()) {
			sb.appendln("/** {@inheritDoc} */");
			sb.appendln("@Override");
			sb.appendln("public void setUp() throws Exception {");
			sb.appendln("  super.setUp();");
		} else {
			sb.appendln("/** Configuracoes iniciais. */");
			sb.appendln("@Before");
			sb.appendln("public void setUp() {");
		}
		sb.appendln("  %s = new %s();", varNameForClassUnderTest, classUnderTest.getType());

		if (classUnderTest.isAtDao()) {
			sb.appendln("%s.setEntityManager(getEntityManager());", varNameForClassUnderTest);
		}

		sb.appendln("}");
		return sb.toString();
	}

	String generateFields() {
		final SourceBuilder sb = new SourceBuilder();
		final Type type = classUnderTest.getType();
		sb.appendln();
		sb.appendJavaDoc("Classe sendo testada {@link %s}.", type);
		sb.appendln("private %s %s;", type, type.getVariableName());
		return sb.toString();
	}

	private boolean ignoreField(Field field) {
		if (field.isStatic())
			return true;

		if (!field.isPrivate())
			return true;

		for (final String annotation : field.getAnnotations()) {
			if (annotation.equals("In") || annotation.equals("EJB")) {
				return false;
			}
		}

		return true;
	}

	String generateImports() {
		final SourceBuilder sb = new SourceBuilder();

		sb.appendln();
		//include all imports of class under test
		for (final String imp : classUnderTest.getImports()) {
			sb.appendln("import %s;", imp);
		}

		sb.appendln("import static org.easymock.EasyMock.*;");
		sb.appendln("import org.junit.*;");
		sb.appendln("import java.util.*;");
		sb.appendln("import static org.junit.Assert.*;");
		if (classUnderTest.isAtView()) {
			sb.appendln("import br.gov.esaf.sgc.view.JsfTestCase;");
		}
		if (classUnderTest.isAtDao()) {
			sb.appendln("import br.gov.esaf.sgc.dao.impl.HibernateTestCase;");
		}
		return sb.toString();
	}

	String generateClassEnd() {
	    SourceBuilder sb = new SourceBuilder();
	    if (GlobalFlags.isNewHashMapUsed()) {
            sb.appendJavaDoc("Criar um HashMap e colocar a chave/valor passados nele."
                    + "\n @param <K> tipo base da chave"
                    + "\n @param <V> tipo base do valor"
                    + "\n @param key chave"
                    + "\n @param value valor"
                    + "\n @return o HashMap criado. ");
            sb.appendln("private <K, V> Map<K, V> newHashMap(K key, V value) {");
            sb.appendln("    Map<K, V> ret = new HashMap<K, V>();");
            sb.appendln("    ret.put(key, value);");
            sb.appendln("    return ret;");
            sb.appendln("}");
	    }

	    if (GlobalFlags.isNewHashSet()) {
	        sb.appendJavaDoc("Criar um HashSet e colocar o valor passado nele."
	            + "\n @param <T> tipo base do valor"
	            + "\n @param value valor"
	            + "\n @return o HashSet criado. ");
	        sb.appendln("private <V> Set<V> newHashSet(V value) {");
	        sb.appendln("    Set<V> ret = new HashSet<V>();");
	        sb.appendln("    ret.add(value);");
	        sb.appendln("    return ret;");
	        sb.appendln("}");
	    }
	    sb.appendln("}");
		return sb.toString();
	}

	String generateClassStart() {
		final SourceBuilder sb = new SourceBuilder();
		sb.appendln();
		String extendz = "";
		if (classUnderTest.isAtView()) {
			extendz = "extends JsfTestCase";
		} else if (classUnderTest.isAtDao()) {
			extendz = "extends HibernateTestCase";
		}

		sb.appendJavaDoc("Testes unitarios para a classe {@link %s}.", classUnderTest
				.getType());
		sb.appendln("public class Test%s %s {", classUnderTest.getType(), extendz);

		return sb.toString();
	}

	String generatePackageName() {
		return "package " + classUnderTest.getPackageName() + ";\n";
	}

}
