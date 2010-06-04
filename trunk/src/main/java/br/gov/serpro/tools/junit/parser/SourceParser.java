package br.gov.serpro.tools.junit.parser;

import java.io.InputStream;

import br.gov.serpro.tools.junit.model.JavaClass;

public interface SourceParser {

	void parse(InputStream inputStream) throws ParseException;

	JavaClass getSource();

}
