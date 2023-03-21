package de.cuioss.portal.configuration;

/**
 * @author Sven Haag
 */
public interface Reloadable {

    /**
     * Callback method to initialize a reload of the perhaps cached data, basically telling e.g a configuration
     * source it should read from its source again as the file content might have changed. Only makes sense for
     * changeable files, i.e. from the file system and not from the classpath.
     */
    default void reload() {
    }
}
