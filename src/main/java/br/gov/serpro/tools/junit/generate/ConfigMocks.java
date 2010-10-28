package br.gov.serpro.tools.junit.generate;

/**
 * The config mocks method section.
 */
public class ConfigMocks extends MethodSection {

    /**
     * Description.
     */
    private String description;

    /**
     * Set description.
     * @param description description
     */
    public final void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public final String getDescription() {
        return this.description;
    }

}