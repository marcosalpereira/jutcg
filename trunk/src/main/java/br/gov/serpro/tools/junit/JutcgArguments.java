package br.gov.serpro.tools.junit;

import java.io.File;

/**
 * Argumentos passados a aplicacao.
 */
class JutcgArguments {
	private static final int MIN_ARGUMENT_COUNT = 1;

	private File javaSourceFile;
	private File configFile;

	/**
	 * Fazer o parse dos argumentos.
	 * @param cls classe principal da aplicacao
	 * @param args argumentos passados
	 * @return os argumentos
	 * @throws ArgumentException em caso de erro
	 */
	static JutcgArguments parseArguments(Class<?> cls, String[] args) throws ArgumentException {

		if (args.length < MIN_ARGUMENT_COUNT) {
			throw new ArgumentException(getSintax(cls));
		}
		JutcgArguments arguments = new JutcgArguments();

		arguments.javaSourceFile = arguments.getFile(args[0]);
		if (!arguments.javaSourceFile.canRead()) {
			throw new ArgumentException(arguments.javaSourceFile + " can't be read");
		}
		if (args.length > 1) {
			arguments.configFile = arguments.getFile(args[1]);
		} else {
			arguments.configFile = arguments.getFile("config.properties");
		}
		if (!arguments.configFile.canRead()) {
			throw new ArgumentException(arguments.configFile + " can't be read");
		}

		return arguments;
	}

    private File getFile(final String filename) {
        final File externalFile = new File(filename);
        if (externalFile.exists()) {
            return externalFile;
        }

        final String fullname = NewTestCase.class.getResource(filename).getFile();
        final File file = new File(fullname);
        return file;
    }

	private static String getSintax(Class<?> cls) {
		return "Sintaxe: " + cls.getName()
		        + " <ArquivoFonteJava> <ConfigProperties>";
	}

	public File getConfigFile() {
	    return configFile;
    }

	public File getJavaSourceFile() {
	    return javaSourceFile;
    }

}

