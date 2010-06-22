package br.gov.serpro.tools.junit;

import java.io.File;
import java.io.IOException;

import br.gov.serpro.tools.junit.generate.TestCaseGenerator;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.parser.JsmgJavaSourceParser;
import br.gov.serpro.tools.junit.parser.ParseException;

public class NewTestCase {

	public static void main(String[] args) throws ParseException, IOException {

		//args = new String[] {"/CampoAtuacaoBusinessBean.java.txt"};

		final NewTestCase newTestCase = new NewTestCase();

		if (!newTestCase.validarArgumentos(args)) {
			return;
		}

		final JsmgJavaSourceParser parser = new JsmgJavaSourceParser();
		final JavaClass javaClass = parser.parse(getFile(args[0]));
		final String testCase = new TestCaseGenerator(javaClass).generate();
		System.out.println(testCase);

	}

	public boolean validarArgumentos(String[] args) {
		if (args.length == 0) {
			System.err.println("Sintaxe: " + getClass().getName() + " <ArquivoFonteJava>");
			return false;
		}
		return true;
	}

	private static File getFile(String filename) throws IOException {
	    File externalFile = new File(filename);
	    if (externalFile.exists()) {
	        return externalFile;
	    }

		final String fullname = NewTestCase.class.getResource(filename).getFile();
		final File file = new File(fullname);
		return file;
	}

}