package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.tools.collect.CollectionLiterals.immutableSet;
import static de.cuioss.tools.string.MoreStrings.isEmpty;

import java.util.Optional;
import java.util.Set;

import de.cuioss.portal.configuration.ConfigurationSource;
import de.cuioss.portal.configuration.impl.PropertiesConfigurationProvider;
import de.cuioss.portal.configuration.source.ConfigurationSourceResolver;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.logging.CuiLogger;

/**
 * 
 */
public class JavaPropertiesConfigurationSourceResolver implements ConfigurationSourceResolver {

    private static final String SUFFIX = "properties";
    private static final String MSG_LOAD_ERROR = "Portal-519: Unable to load configuration file: {}, due to: {}";
    private static final CuiLogger LOGGER = new CuiLogger(JavaPropertiesConfigurationSourceResolver.class);

    @Override
    public Optional<ConfigurationSource> resolve(FileLoader source) {
        if ((null == source) || isEmpty(source.getFileName().getOriginalName())) {
            LOGGER.debug("Nothing to load found");
            return Optional.empty();
        }

        final var suffix = source.getFileName().getSuffix().toLowerCase();
        if (SUFFIX.equals(suffix)) {
            LOGGER.debug("Found properties file {}", source);
            if (!source.isReadable()) {
                LOGGER.error(MSG_LOAD_ERROR, source, "not readable");
                return Optional.empty();
            }
            return Optional.of(new PropertiesConfigurationProvider(source));
        }

        LOGGER.debug("Given file seems not to represent a properties file: {}", source);
        return Optional.empty();
    }

    @Override
    public Set<String> supportedSuffixes() {
        return immutableSet(SUFFIX);
    }

}
