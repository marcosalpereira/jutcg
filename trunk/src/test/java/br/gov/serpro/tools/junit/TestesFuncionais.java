package br.gov.serpro.tools.junit;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import br.gov.serpro.tools.junit.generate.TestCaseGenerator;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.parser.JsmgJavaSourceParser;
import br.gov.serpro.tools.junit.parser.ParseException;
import br.gov.serpro.tools.junit.parser.SourceParser;

public class TestesFuncionais {

    @Test
    public final void testViewSemExtensaoFluxo() throws IOException, ParseException {
        assertContentsEquals("/view.semExtensaoFluxo.java");
    }

    @Test
    public final void testViewComExtensaoFluxo() throws IOException, ParseException {
        assertContentsEquals("/view.comExtensaoFluxo.java");
    }

    @Test
    public final void testBusinessDelegateVoid() throws IOException, ParseException {
        assertContentsEquals("/business.delegateVoid.java");
    }

    @Test
    public final void testBusinessDelegateReturning() throws IOException, ParseException {
        assertContentsEquals("/business.delegateReturning.java");
    }

    @Test
    public final void testBusinessNonVoidInvocation() throws IOException, ParseException {
        assertContentsEquals("/business.nonVoidInvocation.java");
    }

    @Test
    public final void testDao() throws IOException, ParseException {
        assertContentsEquals("/dao.java");
    }

    @Test
    public final void testViewValidator() throws IOException, ParseException {
        assertContentsEquals("/view.validator.java");
    }

    /**
     * @param inputFile input file
     * @throws ParseException em caso de erro
     * @throws IOException em caso de erro
     */
    private void assertContentsEquals(String inputFile) throws ParseException,
        IOException {
        final SourceParser parser = new JsmgJavaSourceParser();
        final JavaClass javaClass = parser
            .parse(getFile(inputFile
                + ".txt"));
        final TestCaseGenerator testCaseGenerator = new TestCaseGenerator(javaClass);
        assertEquals(getContents(inputFile
            + ".expected"), testCaseGenerator
            .generate());
    }

    public String getContents(String resource) throws IOException {
        final InputStream is = this
            .getClass()
            .getResourceAsStream(resource);

        StringBuilder sb = new StringBuilder();
        if (is != null) {
            String line;

            try {
                // BufferedReader reader = new BufferedReader(new
                // InputStreamReader(is, "UTF-8"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader
                    .readLine()) != null) {
                    sb
                        .append(line)
                        .append("\n");
                }
            } finally {
                is
                    .close();
            }
            return sb
                .toString();
        } else {
            return "";
        }
    }

    private File getFile(String filename) throws IOException {
        final String fullname = this
            .getClass()
            .getResource(filename)
            .getFile();
        final File file = new File(fullname);
        return file;
    }

}
