package br.gov.serpro.tools.junit.model;

/**
 * A java member visibility restriction.
 */
public enum Protection {
    DEFAULT, PRIVATE, PROTECTED, PUBLIC;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
