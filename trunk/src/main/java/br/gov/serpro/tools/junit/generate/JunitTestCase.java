package br.gov.serpro.tools.junit.generate;

import java.util.LinkedHashSet;
import java.util.Set;

import br.gov.serpro.tools.junit.util.SourceBuilder;

/**
 * Represents a test case written in JUnit.
 */
public class JunitTestCase {
    private String name;
    private String packageName;
    private final Set<String> imports = new LinkedHashSet<String>();
    private Set<TestMethod> testMethods = new LinkedHashSet<TestMethod>();
    private Set<TestField> fields = new LinkedHashSet<TestField>();
    private String javaDoc;
    private String parent;
    private final StringBuilder generalCode = new StringBuilder();

    public void setJavaDoc(final String format, final Object... args) {
        this.javaDoc = String.format(format, args);
    }

    public Set<TestField> getFields() {
        return this.fields;
    }

    public Set<String> getImports() {
        return this.imports;
    }

    public Set<TestMethod> getTestMethods() {
        return this.testMethods;
    }

    public String getName() {
        return this.name;
    }

    public String getPackage() {
        return this.packageName;
    }

    public void setFields(final Set<TestField> fields) {
        this.fields = fields;
    }

    public void setTestMethods(final Set<TestMethod> methods) {
        this.testMethods = methods;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setPackage(final String packageName) {
        this.packageName = packageName;
    }

    public void setParent(final String string) {
        this.parent = string;
    }

    public String asCode() {
        final SourceBuilder sb = new SourceBuilder();
        sb.appendln("package %s;", this.getPackage());
        sb.appendln();
        for (final String imp : this.getImports()) {
            sb.appendln("import %s;", imp);
        }
        sb.appendln();
        sb.appendJavaDoc(this.javaDoc);
        sb.appendln("public class %s %s {", this.getName(), this.getExtends());
        for (final TestField field : getFields()) {
            sb.appendln();
            sb.append(field.asCode());
        }

        sb.append(generalCode.toString());


        for (final TestMethod method : getTestMethods()) {
            sb.appendln();
            sb.append(method.asCode());
        }
        sb.appendln("}");

        return sb.toString();
    }

    private String getExtends() {
        if (this.parent == null) {
	        return "";
        }
        return "extends " + this.parent;
    }

	public void addGeneralCode(String generalCode) {
		this.generalCode.append(generalCode);
    }

}
