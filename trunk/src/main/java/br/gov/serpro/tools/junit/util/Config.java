package br.gov.serpro.tools.junit.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Config.
 */
public final class Config {

    /**
     * Resource bundle.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle("config");

    /**
     * Construtor.
     */
    private Config() {
    }

    /**
     * Get an property.
     * @param key key
     * @return the value of property
     */
    public static String getString(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (final MissingResourceException e) {
            return null;
        }
    }
}
