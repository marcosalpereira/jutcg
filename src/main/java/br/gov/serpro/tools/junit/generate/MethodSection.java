package br.gov.serpro.tools.junit.generate;

import java.util.ArrayList;
import java.util.List;

import br.gov.serpro.tools.junit.util.SourceBuilder;

/**
 * A test method section.
 */
public abstract class MethodSection {
    /**
     * Code.
     */
    private final List<String> code = new ArrayList<String>();

    /**
     * Returns method section description.
     *
     * @return method section description.
     */
    protected abstract String getDescription();

    /**
     * Add code to section.
     * @param format
     *            format
     * @param args
     *            args
     * @return this
     */
    public final MethodSection addCode(final String format,
            final Object... args) {
        this.code.add(String.format(format, args));
        return this;
    }

    /**
     * @return if has some code.
     */
    public final boolean isNotEmpty() {
        return !this.code.isEmpty();
    }

    /**
     * @return as code.
     */
    public final String asCode() {
        final SourceBuilder sb = new SourceBuilder();
        if (getDescription() != null) {
            sb.appendln("\n// " + getDescription());
        }
        for (final String s : this.code) {
            sb.appendln(s);
        }
        return sb.toString();
    }

}
