package de.cuioss.portal.configuration.impl.source;

import static de.cuioss.tools.string.MoreStrings.isEmpty;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import de.cuioss.portal.configuration.FileConfigurationSource;
import de.cuioss.portal.configuration.impl.PropertiesConfigurationProvider;
import de.cuioss.portal.configuration.yaml.YamlConfigurationProvider;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.logging.CuiLogger;
import lombok.experimental.UtilityClass;

/**
 * Provides utilities for loading / managing Resources that are used for
 * obtaining actual configurations
 *
 * @author Oliver Wolff
 */
@UtilityClass
public class LoaderUtils {

    private static final String MSG_LOAD_ERROR = "Portal-519: Unable to load configuration file: {}, due to: {}";

    private static final CuiLogger log = new CuiLogger(LoaderUtils.class);

    private static final String FILETYPE_NOT_PROVIDED_MSG = "Portal-151: Unsupported configuration file type: {}. Supported is: yml, yaml, properties";

    /**
     * Loads the the content of a given {@link FileConfigurationSource}
     *
     * @param source to be loaded from, supported is: yml, yaml, properties
     *
     * @return the {@link Map} of the contained configuration
     */
    public static Map<String, String> loadConfigurationFromSource(final FileConfigurationSource source) {
        if (null == source || isEmpty(source.getPath())) {
            log.debug("Nothing to load found");
            return Collections.emptyMap();
        }

        final var ymlProvider = YamlConfigurationProvider.createFromFile(source);
        if (ymlProvider.isPresent()) {
            return ymlProvider.get().getConfigurationMap();
        }

        final var propertiesProvider = loadPropertiesFromFile(FileLoaderUtility.getLoaderForPath(source.getPath()));
        if (propertiesProvider.isPresent()) {
            return propertiesProvider.get().getConfigurationMap();
        }

        log.warn(FILETYPE_NOT_PROVIDED_MSG, source);
        return Collections.emptyMap();
    }

    /**
     * Loads the the content of a given {@link FileLoader}
     *
     * @param source to be loaded from, supported is: yml, yaml, properties
     *
     * @return the {@link Map} of the contained configuration
     */
    public static Map<String, String> loadConfigurationFromSource(final FileLoader source) {
        final var ymlProvider = YamlConfigurationProvider.createFromFile(source);
        if (ymlProvider.isPresent()) {
            return ymlProvider.get().getConfigurationMap();
        }

        final var propertiesProvider = loadPropertiesFromFile(source);
        if (propertiesProvider.isPresent()) {
            return propertiesProvider.get().getConfigurationMap();
        }

        log.warn(FILETYPE_NOT_PROVIDED_MSG, source);
        return Collections.emptyMap();
    }

    /**
     * @param source identifying the possible properties file.
     *
     * @return an {@link Optional} on a {@link PropertiesConfigurationProvider} in
     *         case the given source references a readable files ending with
     *         "properties", empty Optional otherwise.
     */
    private static Optional<PropertiesConfigurationProvider> loadPropertiesFromFile(final FileLoader source) {
        if (null == source || isEmpty(source.getFileName().getOriginalName())) {
            log.debug("Nothing to load found");
            return Optional.empty();
        }

        final var suffix = source.getFileName().getSuffix().toLowerCase();
        if ("properties".equals(suffix)) {
            log.debug("Found properties file {}", source);
            if (!source.isReadable()) {
                log.error(MSG_LOAD_ERROR, source, "not readable");
                return Optional.empty();
            }
            return Optional.of(new PropertiesConfigurationProvider(source));
        }

        log.debug("Given file seems not to represent a yml file: {}", source);
        return Optional.empty();
    }
}
