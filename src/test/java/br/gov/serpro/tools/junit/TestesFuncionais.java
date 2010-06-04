package br.gov.serpro.tools.junit;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import br.gov.serpro.tools.junit.parser.JsmgJavaSourceParser;
import br.gov.serpro.tools.junit.parser.ParseException;
import br.gov.serpro.tools.junit.parser.SourceParser;

public class TestesFuncionais {

    @Test
    public final void testGenerateView() throws IOException, ParseException {
        final SourceParser parser = new JsmgJavaSourceParser();
        parser.parse(this.getClass().getResourceAsStream("/view.java.txt"));
        final TestCaseGenerator testCaseGenerator = new TestCaseGenerator(parser.getSource());
        assertEquals(getContents("/view.java.expected"), testCaseGenerator.generate());
    }

    @Test
    public final void testGenerateBusiness() throws IOException, ParseException {
        final SourceParser parser = new JsmgJavaSourceParser();
        parser.parse(this.getClass().getResourceAsStream("/business.java.txt"));
        final TestCaseGenerator testCaseGenerator = new TestCaseGenerator(parser.getSource());
        assertEquals(getContents("/business.java.expected"), testCaseGenerator.generate());
    }

    @Test
    public final void testGenerateDao() throws IOException, ParseException {
        final SourceParser parser = new JsmgJavaSourceParser();
        parser.parse(this.getClass().getResourceAsStream("/dao.java.txt"));
        final TestCaseGenerator testCaseGenerator = new TestCaseGenerator(parser.getSource());
        assertEquals(getContents("/dao.java.expected"), testCaseGenerator.generate());
    }

    public String getContents(String resource) throws IOException {
        final InputStream is = this.getClass().getResourceAsStream(resource);

        StringBuilder sb = new StringBuilder();
        if (is != null) {
            String line;

            try {
                //BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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


}
