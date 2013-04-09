package br.gov.serpro.tools.junit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import br.gov.serpro.tools.junit.generate.JunitTestCase;
import br.gov.serpro.tools.junit.generate.TestCaseGenerator;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.parser.JsmgJavaSourceParser;
import br.gov.serpro.tools.junit.parser.ParseException;
import br.gov.serpro.tools.junit.parser.SourceParser;

/**
 * Entry point of junit test case generation.
 */
public final class NewTestCase {

	private NewTestCase() {
	}

	public static void main(final String[] args) throws ParseException, ArgumentException,
	        IOException {
		final JutcgArguments arguments = JutcgArguments.parseArguments(NewTestCase.class, new String[] {"/home/54706424372/cvsviews/36312esaf/01-Sistema/06-Implementacao/01-Aplicacao/SGC-Esaf/SGC-Internet/src/main/java/br/gov/esaf/sgc/view/DigitarRecursoProvaManualBean.java", "/home/54706424372/dev/java/src/jutcg/src/main/resources/config.properties"});

		if (arguments.getConfigFile() != null) {
			loadProperties(arguments.getConfigFile());
		}

		final SourceParser parser = new JsmgJavaSourceParser();
		final JavaClass javaClass = parser.parse(arguments.getJavaSourceFile());
		final JunitTestCase testCase = new TestCaseGenerator(javaClass).generate();
		System.out.println(testCase.asCode());

	}

	private static void loadProperties(File configFile) throws IOException {
		final FileInputStream propFile = new FileInputStream(configFile);
		final Properties p = new Properties(System.getProperties());
		p.load(propFile);
		System.setProperties(p);
	}

}