package de.cuioss.portal.configuration.source;

import java.util.Optional;
import java.util.Set;

import de.cuioss.portal.configuration.ConfigurationSource;
import de.cuioss.tools.io.FileLoader;

/**
 * SPI for resolving different types of {@link ConfigurationSource}s
 */
public interface ConfigurationSourceResolver {

    /**
     * @param source must not be null
     * @return the concrete {@link ConfigurationSource} if available.
     */
    Optional<ConfigurationSource> resolve(FileLoader source);

    /**
     * @return the supported file-suffixes
     */
    Set<String> supportedSuffixes();

}
