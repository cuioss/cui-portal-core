package de.cuioss.portal.configuration;

import java.io.IOException;
import java.util.Map;

/**
 * Configuration property persistence enabler. Extends
 * {@linkplain ConfigurationSource} in order to be able to decide what should be
 * saved and what not. Each {@linkplain ConfigurationStorage} should only store
 * things they provide.
 *
 * @author Sven Haag
 */
public interface ConfigurationStorage extends ConfigurationSource {

    /**
     * Save configuration while overwriting existing properties.
     *
     * @param map
     * @throws IOException
     */
    void updateConfigurationMap(Map<String, String> map) throws IOException;

    /**
     * Save configuration property. Overwrites an existing property or creates a new
     * one.
     *
     * @param key
     * @param value
     * @throws IOException
     */
    void updateConfigurationProperty(String key, String value) throws IOException;
}
