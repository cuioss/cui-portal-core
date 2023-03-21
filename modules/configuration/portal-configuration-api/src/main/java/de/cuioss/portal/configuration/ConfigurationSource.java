package de.cuioss.portal.configuration;

import java.util.Map;

import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * A {@linkplain ConfigurationSource} returns a {@linkplain Map} of concrete configurations.
 *
 * Alternatively a {@link FileConfigurationSource} or {@link ConfigSource} could be used instead.
 *
 * @author Oliver Wolff
 */
public interface ConfigurationSource {

    /**
     * @return the configuration map representing the configurations under control of the concrete
     *     provider.
     *     The map is immutable.
     */
    Map<String, String> getConfigurationMap();
}
