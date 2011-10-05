package br.gov.serpro.tools.junit.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.gov.serpro.tools.junit.model.Field;
import br.gov.serpro.tools.junit.model.Flow;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.model.Method;
import br.gov.serpro.tools.junit.model.Protection;
import br.gov.serpro.tools.junit.model.Type;
import br.gov.serpro.tools.junit.util.GeneratorHelper;

/**
 * Class for generating test cases.
 */
public class TestCaseGenerator {
    /**
     * Sufix for DAOs layer implementers.
     */
    private static final String IMPL_DAO_SUFIX = "DaoBean";

    /**
     * Class under test.
     */
    private final JavaClass classUnderTest;
    /**
     * Dependencies of class under test.
     */
    private final List<Field> dependencies;
    /**
     * Methods selected for testing.
     */
    private final List<Method> selectedMethods;
    /**
     * Variable name for class under test.
     */
    private final String varNameForClassUnderTest;

    /**
     * Test case being generated.
     */
    private final JunitTestCase testCase;

    /**
     * Constructor.
     * @param classUnderTest class under test
     */
    public TestCaseGenerator(final JavaClass classUnderTest) {
        this.testCase = new JunitTestCase();
        this.classUnderTest = classUnderTest;
        this.dependencies = selectDependencies();
        this.selectedMethods = selectMethods();
        this.varNameForClassUnderTest = classUnderTest.variableNameForType();
    }

    /**
     * Start test case generator.
     * @return test case generated.
     */
    public JunitTestCase generate() {
        generatePackageName();
        generateImports();
        generateJavadoc();
        generateName();
        generateParent();
        generateFields();
        generateCreateMockMethods();
        generateAbstractImpls();
        generateSetup();
        generateMethods();
        return this.testCase;
    }

    /**
     * Create mock methods for each dependency of class under test.
     */
    private void generateCreateMockMethods() {
        final Set<TestMethod> methods = this.testCase.getTestMethods();
        for (final Field dependency : this.dependencies) {
            if (this.classUnderTest.existsAnyInvocation(dependency)) {
                methods.add(generateCreateMockMethod(dependency));
            }
        }
    }

    /**
     * Create an method that create an mock for dependency informed.
     * @param dependency dependency
     * @return the method created
     */
    private TestMethod generateCreateMockMethod(final Field dependency) {
        final TestMethod method = new TestMethod();
        method.setName("criarMock" + dependency.getType());
        method.setJavaDoc("Cria o mock {@link %s} e seta na classe sendo testada.\n"
                + "@return o mock criado", dependency.getType());
        method.setProtection(Protection.PRIVATE);
        method.setType(dependency.getType());

        method.getGeneralCode().addCode("  %1$s mock = createStrictMock(%1$s.class);",
                dependency.getType());
        method.getGeneralCode().addCode("  %s.set%s(mock);", this.varNameForClassUnderTest,
                GeneratorHelper.upperCaseFirstChar(dependency.getName()));
        method.getGeneralCode().addCode("  return mock;");

        return method;
    }

    /**
     * Select the apropriated methods that needs to be tested.
     * @return the methods
     */
    private List<Method> selectMethods() {
        final List<Method> methods = this.classUnderTest.getMethods();
        final List<Method> ret = new ArrayList<Method>();
        for (final Method method : methods) {
            if (!isToIgnoreMethod(method)) {
                ret.add(method);
            }
        }
        return ret;
    }

    /**
     * Select the fields that are class dependencies. Normally injected fields.
     * @return the dependecies.
     */
    private List<Field> selectDependencies() {
        final List<Field> fields = this.classUnderTest.getFields();
        final List<Field> ret = new ArrayList<Field>();
        for (final Field field : fields) {
            if (!ignoreField(field)) {
                ret.add(field);
            }
        }
        return ret;
    }

    /**
     * Generate the testing methods.
     */
    private void generateMethods() {
        final Set<TestMethod> methods = this.testCase.getTestMethods();
        for (final Method method : this.selectedMethods) {
            for (final Flow flow : method.getFlows()) {
                methods.add(new TestCaseMethodFlowGenerator(this.testCase, flow,
                        this.dependencies).generate());
            }
        }
    }

    /**
     * Verify if method is not bo tested.
     * @param method method
     * @return <code>true</code> if is a private, getter or setter method
     */
    private boolean isToIgnoreMethod(final Method method) {
        if (method.isPrivate() || method.isGetter() || method.isSetter()) {
            return true;
        }
        return false;
    }

    /**
     * Gerar metodos que a classe de teste precisa implementar.
     * @return o codigo gerado
     */
    private void generateAbstractImpls() {
        if (!this.classUnderTest.isAtDao()) {
            return;
        }
        final TestMethod method = new TestMethod();
        this.testCase.getTestMethods().add(method);

        final String dsxml = this.varNameForClassUnderTest.replace(IMPL_DAO_SUFIX, "")
                + "DS.xml";
        method.getAnnotations().add("Override");
        method.setProtection(Protection.PROTECTED);
        method.setType(new Type("IDataSet"));
        method.setName("getDataSet");
        method.getGeneralCode().addCode("  return recuperarDataSet(\"%s\");", dsxml);
    }

    private void generateSetup() {
        final TestMethod method = new TestMethod();
        this.testCase.getTestMethods().add(method);

        if (this.classUnderTest.isAtView()) {
            method.getAnnotations().add("Override");
            method.setProtection(Protection.PUBLIC);
            method.setName("setUp");
            method.getExceptions().add("Exception");
            method.getGeneralCode().addCode("  super.setUp();");
        } else {
            method.setJavaDoc("Configuracoes iniciais.");
            method.getAnnotations().add("Before");
            method.setProtection(Protection.PUBLIC);
            method.setName("setUp");
        }
        method.getGeneralCode().addCode("  %s = new %s();", this.varNameForClassUnderTest,
                this.classUnderTest.getType());

        if (this.classUnderTest.isAtDao()) {
            method.getGeneralCode().addCode("%s.setEntityManager(getEntityManager());",
                    this.varNameForClassUnderTest);
        }
    }

    private void generateFields() {
        final Type type = this.classUnderTest.getType();
        final TestField field = new TestField();
        this.testCase.getFields().add(field);
        field.setJavaDoc("Classe sendo testada {@link %s}.", type);
        field.setProtection(Protection.PRIVATE);
        field.setType(type);
        field.setName(type.getVariableName());
    }

    private boolean ignoreField(final Field field) {
        if (field.isStatic()) {
	        return true;
        }

        if (!field.isPrivate()) {
	        return true;
        }

        for (final String annotation : field.getAnnotations()) {
            if (annotation.equals("In") || annotation.equals("EJB")) {
                return false;
            }
        }

        return true;
    }

    private void generateImports() {
        final Set<String> imports = this.testCase.getImports();

        // include all imports of class under test
        imports.addAll(this.classUnderTest.getImports());

        imports.add("static org.easymock.EasyMock.*");
        imports.add("org.junit.*");
        imports.add("java.util.*");
        imports.add("static org.junit.Assert.*");
        if (this.classUnderTest.isAtView()) {
            imports.add("br.gov.esaf.sgc.view.JsfTestCase");
        }
        if (this.classUnderTest.isAtDao()) {
            imports.add("br.gov.esaf.sgc.dao.impl.HibernateTestCase");
        }
    }

    // @Deprecated
    // private String generateClassEnd() {
    // final SourceBuilder sb = new SourceBuilder();
    // if (GlobalFlags.isNewHashMapUsed()) {
    // sb
    // .appendJavaDoc("Criar um HashMap e colocar a chave/valor passados nele."
    // + "\n @param <K> tipo base da chave"
    // + "\n @param <V> tipo base do valor"
    // + "\n @param key chave"
    // + "\n @param value valor"
    // + "\n @return o HashMap criado. ");
    // sb
    // .appendln("private <K, V> Map<K, V> newHashMap(K key, V value) {");
    // sb.appendln("    Map<K, V> ret = new HashMap<K, V>();");
    // sb.appendln("    ret.put(key, value);");
    // sb.appendln("    return ret;");
    // sb.appendln("}");
    // }
    //
    // if (GlobalFlags.isNewHashSet()) {
    // sb
    // .appendJavaDoc("Criar um HashSet e colocar o valor passado nele."
    // + "\n @param <T> tipo base do valor"
    // + "\n @param value valor"
    // + "\n @return o HashSet criado. ");
    // sb.appendln("private <V> Set<V> newHashSet(V value) {");
    // sb.appendln("    Set<V> ret = new HashSet<V>();");
    // sb.appendln("    ret.add(value);");
    // sb.appendln("    return ret;");
    // sb.appendln("}");
    // }
    // sb.appendln("}");
    // return sb.toString();
    // }

    private void generateName() {
        this.testCase.setName(String.format("Test%s", this.classUnderTest.getType()));
    }

    private void generateJavadoc() {
        this.testCase.setJavaDoc("Testes unitarios para a classe {@link %s}.",
                this.classUnderTest.getType());
    }

    private void generateParent() {
        if (this.classUnderTest.isAtView()) {
            this.testCase.setParent("JsfTestCase");
        } else if (this.classUnderTest.isAtDao()) {
            this.testCase.setParent("HibernateTestCase");
        }
    }

    void generatePackageName() {
        this.testCase.setPackage(this.classUnderTest.getPackageName());
    }

}
