package br.gov.serpro.tools.junit;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.junit.Test;

import br.gov.serpro.tools.junit.model.JavaClass;
import br.gov.serpro.tools.junit.parser.JsmgJavaSourceParser;
import br.gov.serpro.tools.junit.parser.ParseException;
import br.gov.serpro.tools.junit.parser.SourceParser;


public class TestesFuncionais {

	@Test
	public final void testGenerateView() throws IOException, ParseException {
		final SourceParser parser = new JsmgJavaSourceParser();
		final JavaClass javaClass = parser.parse(getFile("/view.java.txt"));
		final TestCaseGenerator testCaseGenerator = new TestCaseGenerator(
				javaClass);
		assertEquals(getContents("/view.java.expected"), testCaseGenerator
				.generate());
	}

	@Test
	public final void testGenerateBusiness() throws IOException, ParseException {
		final SourceParser parser = new JsmgJavaSourceParser();
		final JavaClass javaClass = parser.parse(getFile("/business.java.txt"));
		final TestCaseGenerator testCaseGenerator = new TestCaseGenerator(
				javaClass);
		assertEquals(getContents("/business.java.expected"), testCaseGenerator
				.generate());
	}

	@Test
	public final void testGenerateDao() throws IOException, ParseException {
		final SourceParser parser = new JsmgJavaSourceParser();
		final JavaClass javaClass = parser.parse(getFile("/dao.java.txt"));
		final TestCaseGenerator testCaseGenerator = new TestCaseGenerator(
				javaClass);
		assertEquals(getContents("/dao.java.expected"), testCaseGenerator
				.generate());
	}

	public String getContents(String resource) throws IOException {
	    FileInputStream stream = new FileInputStream(getFile(resource));
	    try {
	      FileChannel fc = stream.getChannel();
	      MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
	      /* Instead of using default, pass in a decoder. */
	      return Charset.defaultCharset().decode(bb).toString();
	    }
	    finally {
	      stream.close();
	    }

	}

	public String getContents3(String resource) throws IOException {
	    final InputStream is = this.getClass().getResourceAsStream(resource);
	    //FileInputStream input = new FileInputStream(is);
	    byte[] fileData = new byte[is.available()];
	    is.read(fileData);
	    is.close();
	    return new String(fileData, "US-ASCII");
	}

	public String getContents2(String resource) throws IOException {
		final InputStream is = this.getClass().getResourceAsStream(resource);

		StringBuilder sb = new StringBuilder();
		if (is != null) {
			String line;

			try {
				// BufferedReader reader = new BufferedReader(new
				// InputStreamReader(is, "UTF-8"));
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
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

	private File getFile(String filename) throws IOException {
		final String fullname = this.getClass().getResource(filename).getFile();
		final File file = new File(fullname);
		return file;
	}

}
