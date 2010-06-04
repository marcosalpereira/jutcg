package br.gov.serpro.tools.junit;

import java.io.File;
import java.io.FileNotFoundException;

import br.gov.serpro.tools.junit.parser.JsmgJavaSourceParser;
import br.gov.serpro.tools.junit.parser.ParseException;

public class NewTestCase {

	public static void main(String[] args) throws ParseException, FileNotFoundException {

		args = new String[] {"/home/05473574602/workspaces/workspaceTestCode/jutcg/src/main/resources/CampoAtuacaoBusinessBean.java.txt"};

		final NewTestCase newTestCase = new NewTestCase();

		if (!newTestCase.validarArgumentos(args)) {
			return;
		}

		final JsmgJavaSourceParser parser = new JsmgJavaSourceParser();
		parser.parse(new File(args[0]));
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