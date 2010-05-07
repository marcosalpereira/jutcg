package br.gov.serpro.tools.junit;

public class GeneratorHelper {

	public static String lowerCaseFirstChar(String str) {
		if (str == null) {
			return null;
		} else {
			return str.substring(0, 1).toLowerCase() + str.substring(1);
		}
	}

	public static String upperCaseFirstChar(String str) {
		if (str == null) {
			return null;
		} else {
			return str.substring(0, 1).toUpperCase() + str.substring(1);
		}
	}

}
