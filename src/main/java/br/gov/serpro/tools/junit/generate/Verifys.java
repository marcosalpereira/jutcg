package br.gov.serpro.tools.junit.generate;

/**
 * Verify section in a test method.
 */
public class Verifys extends MethodSection {

    @Override
    protected String getDescription() {
        return "checar estados dos mocks";
    }

}