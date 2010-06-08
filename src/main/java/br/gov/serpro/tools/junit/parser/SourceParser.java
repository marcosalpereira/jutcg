package br.gov.serpro.tools.junit.parser;

import java.io.File;

import br.gov.serpro.tools.junit.model.JavaClass;

public interface SourceParser {

	void parse(File file) throws ParseException;

	JavaClass getSource();

}
