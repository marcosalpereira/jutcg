package br.gov.serpro.tools.junit;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import br.gov.serpro.tools.junit.generate.TestCaseGenerator;
import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.parser.JsmgJavaSourceParser;
import br.gov.serpro.tools.junit.parser.ParseException;
import br.gov.serpro.tools.junit.parser.SourceParser;

/**
 * Run all funcional tests of our application.
 */
public class TestesFuncionais {

	@BeforeClass
	public static void configSystemProperties() throws IOException {
		loadProperties(getFile("/config.properties"));
	}

//	@Test
//	public final void testDelegateReturningListArgumentFunction() throws IOException,
//	        ParseException {
//		assertContentsEquals("/business.delegateReturningListArgumentFunction.java");
//	}

	@Test
	public final void testDelegateReturningEntity() throws IOException, ParseException {
		assertContentsEquals("/business.delegateReturningEntity.java");
	}

	@Test
	public final void testDelegateReturningList() throws IOException, ParseException {
		assertContentsEquals("/business.delegateReturningList.java");
	}

	@Test
	public final void testPreserveImports() throws IOException, ParseException {
		assertContentsEquals("/preserveImports.java");
	}

    @Test
    public final void testViewSemExtensaoFluxo() throws IOException, ParseException {
        assertContentsEquals("/view.semExtensaoFluxo.java");
    }

    @Test
    public final void testViewComExtensaoFluxo() throws IOException, ParseException {
        assertContentsEquals("/view.comExtensaoFluxo.java");
    }

    @Test
    public final void testViewComExtensaoFluxoInvocacoesRetornadas() throws IOException,
            ParseException {
        assertContentsEquals("/view.comExtensaoFluxo.invocacaoRetornada.java");
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

    @Test
    public final void testAssertBooleanKnownFieldValue() throws IOException, ParseException {
        assertContentsEquals("/geral.assertKnownBooleanField.java");
    }

    @Test
    public final void testSetKnownFieldInitialValue() throws IOException, ParseException {
        assertContentsEquals("/geral.setKnownFieldInitialValue.java");
    }

    @Test
    public final void testSetKnownFieldInitialValueNull() throws IOException, ParseException {
        assertContentsEquals("/geral.setKnownFieldInitialValueNull.java");
    }

    @Test
    public final void testSetKnownFieldInitialValueNotNull() throws IOException,
            ParseException {
        assertContentsEquals("/geral.setKnownFieldInitialValueNotNull.java");
    }

    @Test
    public final void testSetKnownFieldInvocationNull() throws IOException, ParseException {
        assertContentsEquals("/geral.setKnownFieldInvocationValueNull.java");
    }

    @Test
    public final void testSetKnownFieldEnumInitialValue() throws IOException, ParseException {
        assertContentsEquals("/geral.setKnownFieldEnumInitialValue.java");
    }

    @Test
    public final void testSetKnownFieldEnumInitialValueUsingReflection() throws IOException,
            ParseException {
        assertContentsEquals("/geral.setKnownFieldEnumInitialValueUsingReflection.java");
    }

    @Test
    public final void testSetKnownFieldConstantInitialValueUsingReflection()
            throws IOException, ParseException {
        assertContentsEquals("/geral.setKnownFieldConstantInitialValueUsingReflection.java");
    }

    /**
     * @param inputFile
     *            input file
     * @throws ParseException
     *             em caso de erro
     * @throws IOException
     *             em caso de erro
     */
    private void assertContentsEquals(final String inputFile) throws ParseException,
            IOException {
        final SourceParser parser = new JsmgJavaSourceParser();
        final JavaClass javaClass = parser.parse(getFile(inputFile + ".txt"));
        final TestCaseGenerator testCaseGenerator = new TestCaseGenerator(javaClass);
        assertEquals(getContents(inputFile + ".expected"), testCaseGenerator.generate()
                .asCode());
    }

    public String getContents(final String resource) throws IOException {
        final InputStream is = this.getClass().getResourceAsStream(resource);

        final StringBuilder sb = new StringBuilder();
        if (is != null) {
            String line;

            try {
                // BufferedReader reader = new BufferedReader(new
                // InputStreamReader(is, "UTF-8"));
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

	private static void loadProperties(File configFile) throws IOException {
		final FileInputStream propFile = new FileInputStream(configFile);
		final Properties p = new Properties(System.getProperties());
		p.load(propFile);
		System.setProperties(p);
	}

    private static File getFile(String filename) throws IOException {
        final String fullname = TestesFuncionais.class.getResource(filename).getFile();
        final File file = new File(fullname);
        return file;
    }

}
