package br.gov.serpro.tools.junit.parser;

import java.io.File;

import br.gov.serpro.tools.junit.model.JavaClass;

/**
 * Source parser.
 */
public interface SourceParser {

	/**
	 * @param javaSourceFile java source file. 
	 * @return {@link JavaClass} built from suplied file
	 * @throws ParseException in case of some parsing error
	 */
	JavaClass parse(File javaSourceFile) throws ParseException;
}
