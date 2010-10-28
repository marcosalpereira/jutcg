package br.gov.serpro.tools.junit.model;

import java.util.ArrayList;
import java.util.List;

import br.gov.serpro.tools.junit.util.Config;

/**
 * A class representing a Java source file.
 */
public class JavaClass {
    /**
     * Package name.
     */
    private String packageName;

    /**
     * Class type.
     */
    private Type type;

    /**
     * Class imports.
     */
    private List<String> imports = new ArrayList<String>();

    /**
     * Class methods.
     */
    private List<Method> methods = new ArrayList<Method>();

    /**
     * Class fields.
     */
    private List<Field> fields = new ArrayList<Field>();

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public List<String> getImports() {
        return this.imports;
    }

    public void setImports(final List<String> imports) {
        this.imports = imports;
    }

    public List<Method> getMethods() {
        return this.methods;
    }

    public void setMethods(final List<Method> methods) {
        this.methods = methods;
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public void setFields(final List<Field> fields) {
        this.fields = fields;
    }

    public boolean isAtView() {
        return getPackageName().startsWith(Config.getString("packagePrefix.view"));
    }

    public boolean isAtDao() {
        return getPackageName().startsWith(Config.getString("packagePrefix.dao"));
    }

    public String variableNameForType() {
        if (getType() == null) return null;
        return getType().getVariableName();
    }

    public Field searchField(final String fieldName) {
        for (final Field f : getFields()) {
            if (f.getName().equals(fieldName)) return f;
        }
        return null;
    }

    public List<Method> searchMethods(final String methodName) {
        final List<Method> ret = new ArrayList<Method>();
        for (final Method m : getMethods()) {
            if (m.getName().equals(methodName)) ret.add(m);
        }
        return ret;
    }

    public boolean isAnOverloadedMethod(final Method method) {
        return searchMethods(method.getName()).size() > 1;
    }

    /**
     * Check is exists any invocation for a field.
     *
     * @param field
     *            field
     * @return <code>true</code> if exists any invocation for the dependency
     */
    public boolean existsAnyInvocation(final Field field) {
        for (final Method met : getMethods()) {
            for (final Flow flow : met.getFlows()) {
                for (final FieldMethodInvocation invocation : flow.getInvocations()) {
                    if (field.equals(invocation.getInvokedAtField())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
