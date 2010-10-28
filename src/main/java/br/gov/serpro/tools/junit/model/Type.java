package br.gov.serpro.tools.junit.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Type {
    public static final Type VOID = new Type("void");

    private String name;

    private String variableName;

    private boolean primitive;

    private String fullName;

    private Type[] generic;

    public Type() {

    }

    public Type(final String name) {
        this.name = name;
        this.fullName = name;
        this.setPrimitive(Character.isLowerCase(name.charAt(0)));
    }

    public Type[] getGeneric() {
        if (this.generic != null) return this.generic;

        final int iniGen = this.name.indexOf('<');
        final int lastIniGen = this.name.lastIndexOf('<');

        // if a complex generic, like List<List<?>>
        // we will ignore
        if (iniGen != lastIniGen) return null;

        if (iniGen > 0) {
            final int endGen = this.name.indexOf('>');
            final String sNomes = this.name.substring(iniGen + 1, endGen);
            final String[] aNomes = sNomes.split(",");
            this.generic = new Type[aNomes.length];
            for (int i = 0; i < aNomes.length; i++) {
                final String nome = aNomes[i].trim();
                this.generic[i] = new Type(nome);
            }
        }

        return this.generic;
    }

    /**
     * note: map is considered a collection too.
     *
     * @return <code>true</code> if is a List, Set or Map
     */
    public boolean isCollection() {
        return isList() || isSet() || isMap();
    }

    public String getDefaultCollectionImpl() {
        if (isMap()) return "HashMap";
        if (isSet()) return "HashSet";
        if (isList()) return "ArrayList";
        return null;
    }

    public boolean isSet() {
        return this.name.startsWith("Set");
    }

    public boolean isMap() {
        return this.name.startsWith("Map");
    }

    public boolean isList() {
        return this.name.startsWith("List");
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
        this.variableName = defineVariableName();
    }

    public void setPrimitive(final boolean primitive) {
        this.primitive = primitive;
    }

    public boolean isPrimitive() {
        return this.primitive;
    }

    public String getVariableName() {
        return this.variableName;
    }

    private String defineVariableName() {
        if (this.name == null) {
            return null;
        } else {
            return this.name.substring(0, 1).toLowerCase()
                    + this.name.substring(1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.name;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        if (this.fullName == null) this.fullName = this.name;
        return this.fullName;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Type) {
            final Type that = (Type) obj;
            return new EqualsBuilder().append(this.getFullName(),
                    that.getFullName()).isEquals();
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(795681801, 961628501).append(
                this.getFullName()).toHashCode();
    }

}
