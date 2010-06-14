package br.gov.serpro.tools.junit;

//TODO try to extends AbstractStringBuilder
public class SourceBuilder {

	StringBuilder sb = new StringBuilder();

	public SourceBuilder appendln() {
		sb.append("\n");
		return this;
	}

	public SourceBuilder append(String format, Object... args) {
		sb.append(String.format(format, args));
		return this;
	}

	public SourceBuilder appendln(String format, Object... args) {
		sb.append(String.format(format, args));
		sb.append("\n");
		return this;
	}

	public SourceBuilder appendJavaDoc(String format, Object... args) {
		final String doc = String.format(format, args).replaceAll("\n", "\n * ");
		sb.append("\n/**\n * " + doc + "\n */\n");
		return this;
	}
	
	public SourceBuilder insertLineComment(int offset, String comment) {
	    sb.insert(offset, "// " + comment + "\n");
	    return this;
	}

	public SourceBuilder appendLineComment(String comment) {
		sb.append("// " + comment + "\n");
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return sb.toString();
	}

	public boolean isEmpty() {
		return sb.length() == 0;
	}

}
