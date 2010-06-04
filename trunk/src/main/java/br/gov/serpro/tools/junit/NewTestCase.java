package br.gov.serpro.tools.junit;

import java.io.IOException;

import br.gov.serpro.tools.junit.parser.JsmgJavaSourceParser;
import br.gov.serpro.tools.junit.parser.ParseException;
import br.gov.serpro.tools.junit.parser.SourceParser;

public class NewTestCase {

	public static void main(String[] args) throws ParseException, IOException {

		args = new String[] {"/VinculoProvaFaseDaoBean.java.txt"};

		final NewTestCase newTestCase = new NewTestCase();

		if (!newTestCase.validarArgumentos(args)) {
			return;
		}

		final SourceParser parser = new JsmgJavaSourceParser();

		parser.parse(NewTestCase.class.getClassLoader().getResourceAsStream(args[0]));
		final String testCase = new TestCaseGenerator(parser.getSource()).generate();
		System.out.println(testCase);


	}

	public boolean validarArgumentos(String[] args) {
		if (args.length == 0) {
			System.err.println("Sintaxe: " + getClass().getName() + " <ArquivoFonteJava>");
			return false;
		}
		return true;
	}


}