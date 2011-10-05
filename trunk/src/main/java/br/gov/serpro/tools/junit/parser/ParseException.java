package br.gov.serpro.tools.junit.parser;

/**
 * A parsing exception.
 */
public class ParseException extends Exception {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -6120026955972553545L;

    public ParseException() {
    }

    public ParseException(final String message) {
        super(message);
    }

    public ParseException(final Throwable cause) {
        super(cause);
    }

    public ParseException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
