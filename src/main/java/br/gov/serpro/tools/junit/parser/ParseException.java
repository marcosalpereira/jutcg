package br.gov.serpro.tools.junit.parser;

public class ParseException extends Exception {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -6120026955972553545L;

	public ParseException() {
	}

	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
