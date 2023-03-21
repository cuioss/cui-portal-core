package de.cuioss.portal.configuration.impl;

import static java.util.Objects.requireNonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import de.cuioss.portal.configuration.ConfigurationSource;
import de.cuioss.tools.collect.MapBuilder;
import de.cuioss.tools.io.FileLoader;
import de.cuioss.tools.io.FileLoaderUtility;
import de.cuioss.tools.logging.CuiLogger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Loads properties from a given {@link FileLoader}. It will not cache the content but always reload
 * from the source on {@link #getConfigurationMap()}.
 *
 * <blockquote>
 * This class remains part of the API because it is used by actual application implementations to
 * implement configuration providers, which should not lead to a portal-configuration-impl
 * dependency.
 * </blockquote>
 *
 * @author Oliver Wolff
 */
@EqualsAndHashCode
@ToString
public class PropertiesConfigurationProvider implements ConfigurationSource, Serializable {

    private static final CuiLogger log = new CuiLogger(PropertiesConfigurationProvider.class);

    private static final String PORTAL_503 =
        "Portal-503: Unable to load from properties file described by '{}', due to: '{}'";

    private static final long serialVersionUID = -3827851969415677777L;

    private static final String MSG_READ_ERROR = "The referenced file can not be loaded";

    @Getter
    private final FileLoader fileLoader;

    /**
     * @param fileLoader must not be null and the file must be readable:
     *            {@link FileLoader#isReadable()}
     */
    public PropertiesConfigurationProvider(final FileLoader fileLoader) {
        requireNonNull(fileLoader, "fileLoader");
        this.fileLoader = fileLoader;
        log.info("Loading properties from: {}", fileLoader.getURL());
    }

    /**
     * @param pathName must not be null and the file must be readable:
     *            {@link FileLoader#isReadable()}
     */
    public PropertiesConfigurationProvider(final String pathName) {
        this(FileLoaderUtility.getLoaderForPath(requireNonNull(pathName, "pathName")));
    }

    @Override
    public Map<String, String> getConfigurationMap() {
        final var builder = new MapBuilder<String, String>();
        final var loaded = getAsProperties();
        if (loaded.isPresent()) {
            final var properties = loaded.get();
            for (final Object key : properties.keySet()) {
                final var keyString = String.valueOf(key);
                builder.put(keyString, properties.getProperty(keyString));
            }
        }
        return builder.toImmutableMap();
    }

    /**
     * @return the properties identified by the given {@link FileLoader}. In case the
     *         {@link FileLoader} is not set or does not provide a readable file it will return an
     *         {@link Optional#empty()}. otherwise, e.g. on read errors it will return an empty
     *         {@link Properties}
     */
    public Optional<Properties> getAsProperties() {
        if (null == fileLoader || !fileLoader.isReadable()) {
            log.error(PORTAL_503, fileLoader, MSG_READ_ERROR);
            return Optional.empty();
        }
        final var properties = new Properties();
        try (final var inputStream =
            new BufferedInputStream(fileLoader.inputStream())) {
            properties.load(inputStream);
            return Optional.of(properties);
        } catch (final IOException e) {
            log.error(PORTAL_503, fileLoader, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
