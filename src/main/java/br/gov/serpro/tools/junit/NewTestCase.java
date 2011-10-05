package br.gov.serpro.tools.junit;

import java.io.File;
import java.io.IOException;

import br.gov.serpro.tools.junit.generate.JunitTestCase;
import br.gov.serpro.tools.junit.generate.TestCaseGenerator;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.parser.JsmgJavaSourceParser;
import br.gov.serpro.tools.junit.parser.ParseException;
import br.gov.serpro.tools.junit.parser.SourceParser;

/**
 * Entry point of junit test case generation.
 */
public class NewTestCase {

    public static void main(final String[] args) throws ParseException, IOException {

        //args = new String[] {"/ValidarNotasBean.java.txt"};

        final NewTestCase newTestCase = new NewTestCase();

        if (!newTestCase.validarArgumentos(args)) {
            return;
        }

        final SourceParser parser = new JsmgJavaSourceParser();
        final JavaClass javaClass = parser.parse(getFile(args[0]));
        final JunitTestCase testCase = new TestCaseGenerator(javaClass).generate();
        System.out.println(testCase);

    }

    /**
     * Validar os argumentos.
     * @param args args
     * @return <code>true</code> se os argumentos sao validos.
     */
    public boolean validarArgumentos(final String[] args) {
        if (args.length == 0) {
            System.err.println("Sintaxe: " + getClass().getName() + " <ArquivoFonteJava>");
            return false;
        }
        return true;
    }

    private static File getFile(final String filename) throws IOException {
        final File externalFile = new File(filename);
        if (externalFile.exists()) {
            return externalFile;
        }

        final String fullname = NewTestCase.class.getResource(filename).getFile();
        final File file = new File(fullname);
        return file;
    }

}