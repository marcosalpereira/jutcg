package br.gov.serpro.tools.junit.util;

/**
 * Helpful methods for test method generation.
 */
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

	public static String plural(String name) {
		if (name.endsWith("ao")) {
			return name.substring(0, name.length() - 2) + "oes";
		}
	    return name + "s";
    }

}
