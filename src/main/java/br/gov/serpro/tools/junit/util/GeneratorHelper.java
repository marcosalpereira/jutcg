package br.gov.serpro.tools.junit.util;

public final class GeneratorHelper {

    /**
     * Construtor.
     */
    private GeneratorHelper() {
    }

    public static String lowerCaseFirstChar(final String str) {
        if (str == null) {
            return null;
        } else {
            return str.substring(0, 1).toLowerCase() + str.substring(1);
        }
    }

    public static String upperCaseFirstChar(final String str) {
        if (str == null) {
            return null;
        } else {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }

}
