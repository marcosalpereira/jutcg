package br.gov.serpro.tools.junit.util;

//TODO try to extends AbstractStringBuilder
public class SourceBuilder {

    private final StringBuilder sb = new StringBuilder();

    public SourceBuilder appendln() {
        this.sb.append("\n");
        return this;
    }

    public SourceBuilder append(final String format, final Object... args) {
        this.sb.append(String.format(format, args));
        return this;
    }

    public SourceBuilder appendln(final String format, final Object... args) {
        this.sb.append(String.format(format, args));
        this.sb.append("\n");
        return this;
    }

    public SourceBuilder appendJavaDoc(final String format, final Object... args) {
        final String doc = String.format(format, args).replaceAll("\n", "\n * ");
        this.sb.append("/**\n * " + doc + "\n */\n");
        return this;
    }

    public SourceBuilder insertln(final int offset, final String str) {
        this.sb.insert(offset, str + "\n");
        return this;
    }

    // public SourceBuilder appendLineComment(String comment) {
    // sb.append("// " + comment + "\n");
    // return this;
    // }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.sb.toString();
    }

    public boolean isEmpty() {
        return this.sb.length() == 0;
    }

}
