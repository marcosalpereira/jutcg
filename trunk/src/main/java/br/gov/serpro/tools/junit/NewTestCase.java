package br.gov.serpro.tools.junit;

import java.io.File;

import br.gov.serpro.tools.junit.parser.JavaSourceParser;
import br.gov.serpro.tools.junit.parser.ParseException;
import br.gov.serpro.tools.junit.parser.SourceParser;

public class NewTestCase {

	public static void main(String[] args) throws ParseException {

		args = new String[] {"resources\\ResultadoProvaManualBusinessBean.java.txt"};

		NewTestCase newTestCase = new NewTestCase();

		if (!newTestCase.validarArgumentos(args)) {
			return;
		}

		final SourceParser parser = new JavaSourceParser();
		parser.parse(new File(args[0]));
		String testCase = new TestCaseGenerator(parser.getSource()).generate();
		System.out.println(testCase);

	}

//	private static List<String> readSource(File file) throws IOException {
//		List<String> ret = new ArrayList<String>();
//
//		BufferedReader input = new BufferedReader(new FileReader(file));
//		String line = null;
//		while ((line = input.readLine()) != null) {
//			if (!line.trim().isEmpty()) {
//				ret.add(line.trim());
//			}
//		}
//		input.close();
//        return ret;
//	}

	public boolean validarArgumentos(String[] args) {
		if (args.length == 0) {
			System.err.println("Sintaxe: " + getClass().getName() + " <ArquivoFonteJava>");
			return false;
		}
		return true;
	}


}